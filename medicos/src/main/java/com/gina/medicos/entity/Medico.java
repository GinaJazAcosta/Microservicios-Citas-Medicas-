package com.gina.medicos.entity;

import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.enums.EspecialidadMedico;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.utils.StringCustomUtils;
import com.gina.common.utils.ValoresUnicosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "MEDICOS")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", length = 12 , nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidadMedico;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidadMedico;

    private void validarNoEliminado(){
        if (this.estadoRegistro==EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El médico ya está eliminado ");
    }

    public void actualizarEspecialidad(EspecialidadMedico nuevaEspecialidad){
        validarNoEliminado();
        if(nuevaEspecialidad==null)
            throw new IllegalArgumentException("La especialidad es requerida");
        this.especialidadMedico = nuevaEspecialidad;
    }

    public void actualizarDisponibilidad(DisponibilidadMedico nuevaDisponibilidad){
        validarNoEliminado();
        if(nuevaDisponibilidad==null)
            throw new IllegalArgumentException("La disponibilidad es requerida");
        this.disponibilidadMedico = nuevaDisponibilidad;
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }
    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, String email, String telefono, String cedulaProfesional,
                           EspecialidadMedico especialidadMedico) {

        validarNoEliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional);
        actualizarEspecialidad(especialidadMedico);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email.trim().toLowerCase();
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();
    }

    private void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                              Short edad, String email, String telefono, String cedulaProfesional) {

        StringCustomUtils.validarTamanio(nombre.trim(), 1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno.trim(), 1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno.trim(), 1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email.trim(), 1,100,
                "El nombre es requerido y debe tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono.trim(), 10,10,
                "El nombre es requerido y debe tener 10 dígitos (0-9)");
        StringCustomUtils.validarTamanio(cedulaProfesional.trim(), 12,12,
                "La cedula profesional es requerida y debe tener 12 caracteres");
        ValoresUnicosUtils.validarRangoShort(edad, (short) 18, (short) 100,
                "La edad es requeridad debe de estar en un rango de 18 a 100 años");

        StringCustomUtils.validarTelefono(telefono);
        StringCustomUtils.validarEmail(email);

        if (especialidadMedico==null)
            throw new IllegalArgumentException("La especialidad es requerida");
        if (estadoRegistro==null)
            throw new IllegalArgumentException("El estado es requerido");
        if (disponibilidadMedico==null)
            throw new IllegalArgumentException("La disponibilidad del medico es requerida");

    }
    public String getNombreCompleto() {
        return String.join(" ",
                nombre,
                apellidoPaterno,
                apellidoMaterno);
    }
}
