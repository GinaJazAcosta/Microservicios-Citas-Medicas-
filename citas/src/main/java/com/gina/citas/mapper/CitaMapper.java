package com.gina.citas.mapper;

import com.gina.citas.dto.CitaRequest;
import com.gina.citas.dto.CitaResponse;
import com.gina.citas.entity.Cita;
import com.gina.common.dto.medico.DatosMedico;
import com.gina.common.dto.medico.MedicoResponse;
import com.gina.common.dto.paciente.DatosPaciente;
import com.gina.common.dto.paciente.PacienteResponse;
import com.gina.common.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

    @Override
    public Cita requestAEntidad(CitaRequest request) {
        if(request == null) return null;
        return Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        if(entidad==null) return null;
        return new CitaResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }


    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        if(entidad==null) return null;
        return new CitaResponse(
                entidad.getId(),
                pacienteResponseaDatosPaciente(paciente),
                medicoResponseaDatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }
    private DatosPaciente pacienteResponseaDatosPaciente(PacienteResponse paciente) {
        if (paciente == null) return null;
        return new DatosPaciente(
                paciente.nombre(),
                paciente.numExpediente(),
                paciente.edad() + " años",
                paciente.peso() + " kg",
                paciente.estatura() + " m.",
                String.join(" ",
                        Math.round(paciente.imc() * 100.0) / 100.0 + "",
                        clasificacionIMC(paciente.imc())),
                paciente.telefono()
        );
    }

    private String clasificacionIMC(Double imc){
        if(imc < 18.5) return "Bajo peso";
        if(imc < 25) return "Peso Normal";
        if(imc < 30) return "Sobrepeso";
        if(imc < 35) return "Obesidad grado I";
        if(imc < 40) return "Obesidad grado II";
        return "Obesidad grado III";
    }

    private DatosMedico medicoResponseaDatosMedico(MedicoResponse medico){
        if(medico == null) return null;
        return new DatosMedico(
                medico.nombre(),
                medico.cedulaProfecional(),
                medico.especialidad()
        );
    }
}
