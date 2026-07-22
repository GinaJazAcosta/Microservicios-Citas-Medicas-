package com.gina.medicos.service;

import com.gina.common.dto.medico.MedicoRequest;
import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);
    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
