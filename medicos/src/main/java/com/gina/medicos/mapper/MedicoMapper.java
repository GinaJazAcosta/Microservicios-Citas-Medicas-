package com.gina.medicos.mapper;

import com.gina.common.dto.medico.MedicoRequest;
import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.mapper.CommonMapper;
import com.gina.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {

    @Override
    public Medico requestAEntidad(MedicoRequest request) {
        if (request==null) return null;
        return Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .email(request.email().trim().toLowerCase())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .disponibilidadMedico(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public MedicoResponse entidadAResponse(Medico entidad) {
        if (entidad==null) return null;
        return new MedicoResponse(
                entidad.getId(),
                entidad.getNombreCompleto(),
                entidad.getEdad(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getCedulaProfesional(),
                entidad.getEspecialidadMedico().getDescripcion(),
                entidad.getDisponibilidadMedico().getDescripcion(),
                entidad.getDisponibilidadMedico().getCodigo()
        );
    }
}
