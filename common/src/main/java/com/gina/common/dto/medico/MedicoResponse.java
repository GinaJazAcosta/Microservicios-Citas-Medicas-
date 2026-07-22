package com.gina.common.dto.medico;

public record MedicoResponse(
        Long id,
        String nombre,
        Short edad,
        String email,
        String telefono,
        String cedulaProfecional,
        String especialidad,
        String disponibilidad,
        Long idDisponibilidad
) {
}
