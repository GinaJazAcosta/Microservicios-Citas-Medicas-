package com.gina.pacientes.service;

import com.gina.common.dto.paciente.PacienteRequest;
import com.gina.common.dto.paciente.PacienteResponse;
import com.gina.common.service.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
    PacienteResponse obtenerPacientePorIdSinEstado(Long id);
}
