package com.gina.citas.service;

import com.gina.citas.dto.CitaRequest;
import com.gina.citas.dto.CitaResponse;
import com.gina.common.service.CrudService;

public interface CitaService  extends CrudService<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);
}
