package com.gina.pacientes.service;

import com.gina.common.dto.paciente.PacienteRequest;
import com.gina.common.dto.paciente.PacienteResponse;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import com.gina.pacientes.entity.Paciente;
import com.gina.pacientes.mappers.PacienteMapper;
import com.gina.pacientes.repository.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override //LISTAR PACIENTES ACTIVOS (GET /)
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listando todos los pacientes activos");
        return pacienteRepository
                .findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(pacienteMapper::entidadAResponse)
                .toList();
    }

    @Override //OBTENER PACIENTE POR ESTADO (GET /{id})
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        Paciente paciente = obtenerPacientePorEstado(id, EstadoRegistro.ACTIVO);
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override //OBTENER PACIENTE SIN VALIDAR ESTARDO (GET /id-paciente/{id})
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
        log.info("Obteniendo paciente sin validar estado. Id={}", id);
        return pacienteMapper.entidadAResponse(obtenerPacientePorId(id));
    }

    @Override //REGISTRAR PACIENTE (POST /)
    public PacienteResponse registrar(PacienteRequest request) {
        validarDuplicados(request);
        Paciente paciente = pacienteMapper.requestAEntidad(request);
        paciente.asignarIMC();
        paciente.asignarNumExpediente();
        pacienteRepository.save(paciente);
        log.info("Paciente registrado correctamente. Id={}", paciente.getId());
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override //ACTUALIZAR PACIENTE (PUT /{id})
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerPacientePorEstado(id, EstadoRegistro.ACTIVO);
        // TODO
        // Validar que el paciente no tenga citas
        // CONFIRMADAS o EN_CURSO.
        validarDuplicadosActualizar(id, request);

        paciente.actualizar(
                request.nombre(), request.apellidoPaterno(), request.apellidoMaterno(), request.edad(),
                request.peso(), request.estatura(), request.email(), request.telefono(), request.direccion()

        );

        pacienteRepository.save(paciente);
        log.info("Paciente actualizado correctamente. Id={}", paciente.getId());
        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override // ElLIMINACIÓN LÓGICA PACIENTE (DELETE /{id})
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacientePorEstado(id, EstadoRegistro.ACTIVO);
        // TODO
        // Validar que el paciente no tenga citas
        // CONFIRMADAS o EN_CURSO.
        paciente.eliminar();
        pacienteRepository.save(paciente);
        log.info("Paciente eliminado correctamente. Id={}", id);
    }

    private Paciente obtenerPacientePorId(Long id){
        log.info("Buscando paciente con id: {}", id);
        return pacienteRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Paciente no encontrado con id: " + id));
    }

    private Paciente obtenerPacientePorEstado(Long id, EstadoRegistro estado){
        log.info("Obteniendo paciente con estado: {} e Id: {}", estado, id);
        return pacienteRepository
                .findByIdAndEstadoRegistro(id, estado)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Paciente " + estado + " no encontrado con id: " + id));
    }


    private void validarDuplicados(PacienteRequest request) {
        log.info(("Validando email y teléfono para alta de paciente"));
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un paciente activo con ese correo");
        if (pacienteRepository.existsByTelefonoIgnoreCaseAndEstadoRegistro(request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un paciente activo con ese teléfono");
    }

    private void validarDuplicadosActualizar(Long id, PacienteRequest request) {
        log.info(("Validando email y teléfono para edición de paciente"));
        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un paciente activo con ese correo");
        if (pacienteRepository.existsByTelefonoIgnoreCaseAndEstadoRegistroAndIdNot(request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un paciente activo con ese teléfono");
    }

}
