package com.gina.pacientes.entity;

import com.gina.common.enums.EstadoRegistro;
import com.gina.common.utils.StringCustomUtils;
import com.gina.common.utils.ValoresUnicosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "PACIENTES")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", length = 50, nullable = false)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", length = 50, nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", length = 50, nullable = false)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoRegistro estadoRegistro;

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad,
                           Double peso, Double estatura, String email, String telefono, String direccion) {
        validarNoEliminado();
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, email, telefono, direccion, edad, peso, estatura);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.email = email.toLowerCase().trim();
        this.telefono = telefono.trim();
        this.direccion = direccion.trim();
        this.asignarIMC();
        this.asignarNumExpediente();
    }

    private void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, String email,
                              String telefono, String direccion, Short edad, Double peso, Double estatura) {

        StringCustomUtils.validarTamanio(nombre.trim(), 1,50,
                "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno.trim(), 1,50,
                "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno.trim(), 1,50,
                "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(email.trim(), 1,100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono.trim(), 10,10,
                "El teléfono es requerido y debe tener 10 dígitos (0-9)");
        StringCustomUtils.validarTamanio(direccion.trim(), 1,150,
                "La dirección es requerida y debe tener entre 1 y 150 caracteres");
        ValoresUnicosUtils.validarRangoShort(edad, (short) 1, (short) 100,
                "La edad es requerida debe de estar en un rango de 1 a 100 años");
        ValoresUnicosUtils.validarRangoDouble(peso, 1.0, 200.0,
                "El peso es requerido debe de estar en un rango de 1 a 200 Kg");
        ValoresUnicosUtils.validarRangoDouble(estatura, 1.0, 2.0,
                "La estatura es requerida debe de estar en un rango de 1 a 2 metros");

        StringCustomUtils.validarTelefono(telefono);
        StringCustomUtils.validarEmail(email);
    }
    public void asignarIMC (){
        validarNoEliminado();
        if(peso==null || estatura==null || estatura <= 0){
            this.imc = 0.0;
            return;
        }
        this.imc = this.peso / Math.pow(this.estatura, 2);
    }

    private void validarNoEliminado(){
        if (this.estadoRegistro==EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("El paciente ya está eliminado ");
    }

    public String getNombreCompleto() {
        return String.join(" ",
                nombre,
                apellidoPaterno,
                apellidoMaterno);
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void asignarNumExpediente(){
        validarNoEliminado();
        if(telefono==null){
            this.numExpediente=null;
            return;
        }
        StringBuilder expediente = new StringBuilder(telefono.length() * 2);
        for (char c : this.telefono.toCharArray())
            expediente.append(c).append("X");
        this.numExpediente = expediente.toString();
    }
}
