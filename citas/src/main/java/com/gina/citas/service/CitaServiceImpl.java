package com.gina.citas.service;

import com.gina.citas.dto.CitaRequest;
import com.gina.citas.dto.CitaResponse;
import com.gina.citas.entity.Cita;
import com.gina.citas.enums.EstadoCita;
import com.gina.citas.mapper.CitaMapper;
import com.gina.citas.repository.CitaRepository;
import com.gina.common.clients.MedicoCliente;
import com.gina.common.clients.PacienteCliente;
import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.dto.paciente.PacienteResponse;
import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaMapper citaMapper;
    private final CitaRepository citaRepository;
    private final MedicoCliente medicoCliente;
    private final PacienteCliente pacienteCliente;

    @Override
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(
                        cita -> citaMapper.entidadAResponse(
                                cita,
                                obtenerPacienteSinEstado(cita.getIdPaciente()),
                                obtenerMedicoSinEstado(cita.getIdMedico())
                        )
                ).toList();
    }

    @Override
    public CitaResponse obtenerPorId(Long id) {
        log.info("Listando cita por id");
        Cita cita = obtenerCitaOException(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Dando de alta cita");
        //El paciente existe y está ACTIVO.
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        //El médico existe y está ACTIVO.
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        //El paciente no tiene otra cita activa (PENDIENTE, CONFIRMADA o EN_CURSO).
        validarPacienteSinOtraCita(request.idPaciente());
        //El médico está DISPONIBLE.
        validarMedicoDisponible(medico);
        //El medico no tiene otra cita activa (PENDIENTE, CONFIRMADA o EN_CURSO).
        validarMedicoSinOtraCita(request.idMedico());
        //Fecha presente o futura (LISTO)
        //Está en validar fecha en entidad, se llama al validar datos (al guardar desde el mapper)

        //Síntomas de 20 a 500 caracteres (LISTO)
        //en entidad y request

        //Estado inicial PENDIENTE (LISTO)
        //Al guardar

        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.save(cita);

        medicoCliente.actualizarDisponibilidadMedico(
                cita.getIdMedico(),
                cita.getEstadoCita()
                        .obtenerDisponibilidadResultante()
                        .getCodigo()
        );

        log.info("Cita registrada exitósamente");
        return citaMapper.entidadAResponse(cita, paciente, medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        log.info("Actualizando cita con id: {}", id);

        Cita cita = obtenerCitaOException(id);

        if(!cita.getEstadoCita().esActualizable()){
            throw new IllegalStateException("La cita no permite modificaciones en su estado actual");
        }

        Long idPacienteAnterior = cita.getIdPaciente();
        Long idMedicoAnterior = cita.getIdMedico();

        boolean cambioPaciente = !Objects.equals(idPacienteAnterior, request.idPaciente());
        boolean cambioMedico = !Objects.equals(idMedicoAnterior, request.idMedico());

        PacienteResponse paciente;
        MedicoResponse medico;

        // ===== PACIENTE =====
        if(cambioPaciente){
            paciente = obtenerPacienteActivo(request.idPaciente());
            validarPacienteSinOtraCita(request.idPaciente(), id);
        }else{
            paciente = obtenerPacienteActivo(idPacienteAnterior);
        }

        // ===== MÉDICO =====
        if(cambioMedico){
            medico = obtenerMedicoActivo(request.idMedico());
            validarMedicoDisponible(medico);
            validarMedicoSinOtraCita(request.idMedico(), id);
        }else{
            medico = obtenerMedicoActivo(idMedicoAnterior);
        }

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        // Si cambió el médico, sincronizar disponibilidades
        if (cambioMedico) {
            // Libera al médico anterior
            sincronizarDisponibilidadMedico(idMedicoAnterior, cita.getId());
            // Ocupa al nuevo médico según el estado de la cita
            medicoCliente.actualizarDisponibilidadMedico(
                    cita.getIdMedico(),
                    cita.getEstadoCita()
                            .obtenerDisponibilidadResultante()
                            .getCodigo()
            );
        }

        log.info("Cita actualizada correctamente con id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico
        );
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita con id: {}", id);

        Cita cita = obtenerCitaOException(id);
        if(!cita.getEstadoCita().esEliminable()){
            throw new IllegalStateException("La cita en estado " + cita.getEstadoCita().getDescripcion() + " no puede eliminarse");
        }
        cita.eliminar();
        sincronizarDisponibilidadMedico(cita.getIdMedico(), cita.getId());
        log.info("Cita con id: {} eliminada exitosamente", id);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        log.info("Actualizando estado de la cita con id: {}", idCita);
        Cita cita = obtenerCitaOException(idCita);
        EstadoCita nuevoEstado = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);
        cita.actualizarEstadoCita(nuevoEstado);

        if(nuevoEstado == EstadoCita.FINALIZADA || nuevoEstado == EstadoCita.CANCELADA){
            sincronizarDisponibilidadMedico(cita.getIdMedico(), cita.getId());
        }else{
            medicoCliente.actualizarDisponibilidadMedico(cita.getIdMedico(), nuevoEstado.obtenerDisponibilidadResultante().getCodigo());
        }


        log.info("Estado de la cita con id: {} actualizado correctamente", idCita );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneCitasActivasPaciente(Long idPaciente) {

        return citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneCitasActivasMedico(Long idMedico) {

        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                )
        );
    }


























    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Buscando medico activo con id: {} en el servicio remoto...", id);
        return medicoCliente.obtenerMedicoActivoPorId(id);
    }
    private MedicoResponse obtenerMedicoSinEstado(Long id){
        log.info("Buscando medico sin estado con id: {} en el servicio remoto...", id);
        return medicoCliente.obtenerMedicoSinEstadoPorId(id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id){
        log.info("Buscando paciente activo con id: {} en el servicio remoto...", id);
        return pacienteCliente.obtenerPacienteActivoPorId(id);
    }
    private PacienteResponse obtenerPacienteSinEstado(Long id){
        log.info("Buscando paciente sin estado con id: {} en el servicio remoto...", id);
        return pacienteCliente.obtenerPacienteSinEstadoPorId(id);
    }



    private Cita obtenerCitaOException(Long id){
        log.info("Obteniendo cita con Id: {}", id);
        return citaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cita no encontrada con id: " + id));
    }


    private void validarMedicoDisponible(MedicoResponse medico){

        if(!Objects.equals(
                medico.idDisponibilidad(),
                DisponibilidadMedico.DISPONIBLE.getCodigo())){
            throw new IllegalStateException(
                    "El médico no se encuentra disponible para recibir una nueva cita."
            );
        }
    }

    private void validarPacienteSinOtraCita(Long idPaciente){

        if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.PENDIENTE,
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ))){

            throw new IllegalStateException(
                    "El paciente ya tiene una cita pendiente, confirmada o en curso."
            );
        }
    }

    private void validarMedicoSinOtraCita(Long idMedico){

        if(citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.PENDIENTE,
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ))){

            throw new IllegalStateException(
                    "El médico ya tiene una cita pendiente, confirmada o en curso."
            );
        }
    }

    private void validarPacienteSinOtraCita(Long idPaciente, Long idCita){

        if(citaRepository.existsByIdPacienteAndIdNotAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                idCita,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.PENDIENTE,
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ))){

            throw new IllegalStateException(
                    "El paciente ya tiene otra cita pendiente, confirmada o en curso."
            );
        }
    }

    private void validarMedicoSinOtraCita(Long idMedico, Long idCita){

        if(citaRepository.existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                idCita,
                EstadoRegistro.ACTIVO,
                List.of(
                        EstadoCita.PENDIENTE,
                        EstadoCita.CONFIRMADA,
                        EstadoCita.EN_CURSO
                ))){

            throw new IllegalStateException(
                    "El médico ya tiene otra cita pendiente, confirmada o en curso."
            );
        }
    }

    private void sincronizarDisponibilidadMedico(Long idMedico, Long idCitaExcluir){

        boolean tieneEnCurso =
                citaRepository.existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCita(
                        idMedico, idCitaExcluir, EstadoRegistro.ACTIVO, EstadoCita.EN_CURSO);

        if(tieneEnCurso){
            medicoCliente.actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
            return;
        }

        boolean tienePendienteOConfirmada =
                citaRepository.existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn(
                        idMedico,
                        idCitaExcluir,
                        EstadoRegistro.ACTIVO,
                        List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA)
                );

        if(tienePendienteOConfirmada){
            medicoCliente.actualizarDisponibilidadMedico(
                    idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
            return;
        }

        medicoCliente.actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

}
