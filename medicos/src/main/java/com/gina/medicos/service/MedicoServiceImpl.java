package com.gina.medicos.service;

import com.gina.common.clients.CitaCliente;
import com.gina.common.dto.medico.MedicoRequest;
import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.enums.EspecialidadMedico;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import com.gina.medicos.ServiceUtils;
import com.gina.medicos.entity.Medico;
import com.gina.medicos.mapper.MedicoMapper;
import com.gina.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;

    private final CitaCliente citaCliente;

    @Override //LISTAR MEDICOS ACTIVOS (GET /)
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los medicos activos");
        return medicoRepository
                .findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(medicoMapper::entidadAResponse)
                .toList();
    }

    @Override //OBTENER MÉDICO POR ESTADO (GET /{id})
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        Medico medico = obtenerMedicoPorEstado(id, EstadoRegistro.ACTIVO);
        return medicoMapper.entidadAResponse(medico);
    }

    @Override //OBTENER MEDICO SIN VALIDAR ESTARDO (GET /id-medico/{id})
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Obteniendo médico sin validar estado. Id={}", id);
        return medicoMapper.entidadAResponse(obtenerMedicoPorId(id));
    }

    @Override //REGISTRAR MEDICO (POST /)
    public MedicoResponse registrar(MedicoRequest request) {
        validarDuplicados(request);
        EspecialidadMedico especialidad = EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad());
        Medico medico = medicoMapper.requestAEntidad(request);
        medico.actualizarEspecialidad(especialidad);
        medicoRepository.save(medico);
        log.info("Médico registrado correctamente. Id={}", medico.getId());
        return medicoMapper.entidadAResponse(medico);
    }

    @Override //ACTUALIZAR MEDICO (PUT /{id})
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        Medico medico = obtenerMedicoPorEstado(id, EstadoRegistro.ACTIVO);
        validarMedicoSinCitasActivas(id);
        // Validar que el médico no tenga citas
        // CONFIRMADAS o EN_CURSO.
        validarDuplicadosActualizar(id, request);
        EspecialidadMedico especialidad = EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad());
        medico.actualizar(
                request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(),
                request.edad(), request.email(), request.telefono(),
                request.cedulaProfesional(), especialidad
        );

        medico = medicoRepository.save(medico);
        log.info("Médico actualizado correctamente. Id={}", medico.getId());
        return medicoMapper.entidadAResponse(medico);

    }

    @Override //CAMBIO DE DISPONIBILIDAD RESTRINGIDO
    // (PUT /{idMedico}/disponibilidad/{idDisponibilidad})
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoPorEstado(idMedico, EstadoRegistro.ACTIVO);
        // Validar que el médico no tenga citas
        // CONFIRMADAS o EN_CURSO antes de permitir
        // el cambio manual de disponibilidad.
        //validarMedicoSinCitasActivas(idMedico);
        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);
        DisponibilidadMedico anteriorDisponibilidad = medico.getDisponibilidadMedico();



        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.save(medico);

        log.info("Disponibilidad del médico con id: {} actualizada de {} a {}", idMedico, anteriorDisponibilidad, nuevaDisponibilidad.name());
    }

    @Override // ElLIMINACIÓN LÓGICA MEDICO (DELETE /{id})
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoPorEstado(id, EstadoRegistro.ACTIVO);
        validarMedicoSinCitasActivas(id);
        // Validar que el médico no tenga citas
        // CONFIRMADAS o EN_CURSO.
        medico.eliminar();
        medicoRepository.save(medico);
        log.info("Médico eliminado correctamente. Id={}", id);
    }

    private void validarMedicoSinCitasActivas(Long idMedico){

        log.info("Validando que el médico {} no tenga citas confirmadas o en curso", idMedico);

        if(Boolean.TRUE.equals(citaCliente.tieneCitasActivasMedico(idMedico))){
            throw new IllegalStateException(
                    "El médico tiene citas confirmadas o en curso."
            );
        }
    }


    private Medico obtenerMedicoPorId(Long id){
        return ServiceUtils.obtenerEntidadOException(medicoRepository, id, Medico.class);
    }

    private Medico obtenerMedicoPorEstado(Long id, EstadoRegistro estado){
        log.info("Obteniendo médico con estado: {} e Id: {}", estado, id);
        return medicoRepository
                .findByIdAndEstadoRegistro(id, estado)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Médico " + estado + " no encontrado con id: " + id));
    }


    private void validarDuplicados(MedicoRequest request) {
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo con ese correo");
        if (medicoRepository.existsByTelefonoIgnoreCaseAndEstadoRegistro(request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo con ese teléfono");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo con esa cédula profesional");
    }

    private void validarDuplicadosActualizar(Long id, MedicoRequest request) {
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo con ese correo");
        if (medicoRepository.existsByTelefonoIgnoreCaseAndEstadoRegistroAndIdNot(request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo con ese teléfono");
        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo con esa cédula profesional");
    }
}
