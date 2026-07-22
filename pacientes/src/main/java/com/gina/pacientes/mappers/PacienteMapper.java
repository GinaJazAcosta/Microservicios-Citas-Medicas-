package com.gina.pacientes.mappers;

import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.dto.paciente.PacienteRequest;
import com.gina.common.dto.paciente.PacienteResponse;
import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.mapper.CommonMapper;
import com.gina.pacientes.entity.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {
    @Override
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request==null) return null;
        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .email(request.email().trim().toLowerCase())
                .edad(request.edad())
                .estatura(request.estatura())
                .peso(request.peso())
                .telefono(request.telefono().trim())
                .direccion(request.direccion().trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad==null) return null;
        return new PacienteResponse(
                entidad.getId(),
                entidad.getNombreCompleto(),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDireccion(),
                entidad.getNumExpediente()
        );
    }
}
