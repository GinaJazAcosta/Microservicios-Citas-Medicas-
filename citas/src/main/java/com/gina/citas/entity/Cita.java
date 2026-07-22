package com.gina.citas.entity;

import com.gina.citas.enums.EstadoCita;
import com.gina.common.enums.EstadoRegistro;
import com.gina.common.utils.StringCustomUtils;
import com.gina.common.utils.ValoresUnicosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CITAS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CITA")
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false, length = 500)
    private String sintomas;

    @Column(name = "ESTADO_CITA", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCita estadoCita;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;



    public static Cita crear(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);
        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .fechaCita(fechaCita)
                .sintomas(sintomas)
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
    private static void validarId(Long id, String campo){
        ValoresUnicosUtils.validarLongPositivo(id, "El id del " + campo + " es requerido y dee ser positivo");
    }

    private  static void validarFechaCita(LocalDateTime fechaCita){
        if (fechaCita==null || !fechaCita.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("La fecha de la cita es requerida y debe ser futura");
    }

    public void actualizarEstadoCita(EstadoCita nuevoEstado){
        validarActualizacionPermitida();
        validarNoEliminado();
        if(nuevoEstado==null)
            throw new IllegalArgumentException("El nuevo estado de la cita es requerido");
        if (!this.estadoCita.puedeCambiarA(nuevoEstado))
            throw new IllegalArgumentException("La cita con estado: " + this.estadoCita +
                    " solo puede cambiar a " +this.estadoCita.puedeCambiar());
        this.estadoCita = nuevoEstado;
    }

    public void actualizar(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarActualizacionPermitida();
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);
        this.idPaciente=idPaciente;
        this.idMedico=idMedico;
        this.fechaCita=fechaCita;
        this.sintomas=sintomas.trim();
    }
    public void eliminar(){
        validarEliminacionPermitida();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    private void validarNoEliminado(){
        if (this.estadoRegistro==EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("La cita ya está eliminada");
    }
    private void validarEliminacionPermitida(){
        validarNoEliminado();
        if (!this.estadoCita.isEliminable()){
            throw  new IllegalArgumentException("La cita con estado " + this.estadoCita + "no puede eliminarse");
        }
    }

    private void validarActualizacionPermitida(){
        validarNoEliminado();
        if (!this.estadoCita.isActualizable()){
            throw  new IllegalArgumentException("La cita con estado " + this.estadoCita + "no puede actualizarse");
        }
    }

    public static void validarDatos(Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarId(idPaciente, "paciente");
        validarId(idMedico, "médico");
        validarFechaCita(fechaCita);
        StringCustomUtils.validarTamanio(sintomas, 20, 500, "Los síntomas son requeridos");
    }


}
