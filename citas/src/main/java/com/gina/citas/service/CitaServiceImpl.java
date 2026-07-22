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
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.save(cita);
        log.info("Cita registrada exitósamente");
        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        log.info("Actualizando cita con id: {}", id);
        Cita cita = obtenerCitaOException(id);
        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
        log.info("Cita actualizada con id: {}", id);
        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita con id: {}", id);
        Cita cita = obtenerCitaOException(id);
        citaRepository.delete(cita);
        log.info("Cita con id: {} eliminada exitosamente", id);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        log.info("Actualizando estado de la cita con id: {}", idCita);
        Cita cita = obtenerCitaOException(idCita);
        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));
        log.info("Estado de la cita con id: {} actualizado correctamente", idCita );
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
}
