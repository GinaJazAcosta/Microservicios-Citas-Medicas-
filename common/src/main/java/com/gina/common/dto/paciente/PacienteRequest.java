package com.gina.common.dto.paciente;

import jakarta.validation.constraints.*;

public record PacienteRequest (
        @NotBlank(message="El nombre es requerido")
        @Size(min=1,max=50,message="El nombre es requerido y debe tener entre 1 y 50 caracteres")
        String nombre,

        @NotBlank(message="El apellido paterno es requerido")
        @Size(min=1,max=50,message="El apellido paterno es requerido y debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @NotBlank(message="El apellido materno es requerido")
        @Size(min=1,max=50,message="El apellido materno es requerido y debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @NotNull(message="La edad es requerida")
        @Min(value=1, message="La edad mínima es 1 año")
        @Max(value=100, message="La edad máxima es 100 años")
        Short edad,

        @NotNull(message="El peso es requerido")
        @DecimalMin(value="1.0", message="El peso mínima es 1 Kg")
        @DecimalMax(value="200.0", message="El peso máxima es 200 Kg")
        Double peso,

        @NotNull(message="La estatura es requerida")
        @DecimalMin(value="1.0", message="La estatura mínima es 1 metro")
        @DecimalMax(value="2.0", message="La estatura máxima es 2 metros")
        Double estatura,

        @NotBlank(message="El email es requerido")
        @Size(min=1,max=100,message="El email es requerido y debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message="El teléfono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message="El teléfono es requerido y debe tener 10 dígitos numéricos")
        String telefono,

        @NotBlank(message="La dirección es requerida")
        @Size(min=1,max=150,message="La dirección es requerida y debe tener entre 1 y 150 caracteres")
        String direccion

){
}
