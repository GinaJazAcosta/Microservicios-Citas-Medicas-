package com.gina.common.dto.medico;

import jakarta.validation.constraints.*;

public record MedicoRequest(
        @NotBlank(message="El nombre es requerido")
        @Size(min=1,max=50,message="El nombre es requerido y debe tener entre 1 y 50 caracteres")
        String nombre,

        @NotBlank(message="El apellido paterno es requerido")
        @Size(min=1,max=50,message="El apellido paterno es requerido y debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @NotBlank(message="El apellido materno es requerido")
        @Size(min=1,max=50,message="El apellido materno es requerido y debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        //@NotBlank(message="La edad es requerida")
        @Min(value=18, message="La edad mínima es 18 años")
        @Max(value=100, message="La edad máxima es 100 años")
        Short edad,

        @NotBlank(message="El email es requerido")
        @Size(min=1,max=100,message="El email es requerido y debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message="El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message="El teléfono es requerido y debe tener 10 dígitos numéricos")
        String telefono,

        @NotBlank(message="La cédula profesional es requerida")
        @Size(min=12,max=12,message="La cédula profesional es requerida y debe tener entre 12 caracteres")
        String cedulaProfesional,

        //@NotBlank(message="La especialidad es requerida")
        @Positive(message="La especialidad es requerida y debe ser positiva")
        Long idEspecialidad

) {
}
