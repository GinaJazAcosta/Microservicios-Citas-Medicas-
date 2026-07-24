package com.gina.citas.enums;

import com.gina.common.enums.DisponibilidadMedico;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public enum EstadoCita {
    PENDIENTE(1L, "Pendiente de confirmar", true, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(CONFIRMADA, CANCELADA);
        }
        @Override
        public DisponibilidadMedico obtenerDisponibilidadResultante() {
            return DisponibilidadMedico.NO_DISPONIBLE;
        }
    },
    CONFIRMADA(2L, "Confirmada por el paciente", true, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(EN_CURSO, CANCELADA);
        }
        @Override
        public DisponibilidadMedico obtenerDisponibilidadResultante() {
            return DisponibilidadMedico.NO_DISPONIBLE;
        }
    },
    EN_CURSO(3L, "Paciente llegó a su cita", false, false) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
        @Override
        public DisponibilidadMedico obtenerDisponibilidadResultante() {
            return DisponibilidadMedico.EN_CONSULTA;
        }
    },
    FINALIZADA(4L, "Cita finalizada", false, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
        @Override
        public DisponibilidadMedico obtenerDisponibilidadResultante() {
            return DisponibilidadMedico.DISPONIBLE;
        }
    },
    CANCELADA(5L, "Cita cancelada", false, true) {
        @Override
        public Set<EstadoCita> puedeCambiar() {
            return Set.of();
        }
        @Override
        public DisponibilidadMedico obtenerDisponibilidadResultante() {
            return DisponibilidadMedico.DISPONIBLE;
        }
    };

    private final Long codigo;
    private final String descripcion;
    private final boolean actualizable;
    private final boolean eliminable;

    public abstract Set<EstadoCita> puedeCambiar();
    public abstract DisponibilidadMedico obtenerDisponibilidadResultante();

    public boolean puedeCambiarA(EstadoCita nuevoEstado){
        return this.puedeCambiar().contains(nuevoEstado);
    }
    public static EstadoCita obtenerEstadoCitaPorCodigo(Long codigo) {
        for (EstadoCita e : values()) {
            if (Objects.equals(e.codigo, codigo)) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Código de estado de la cita no válido: " + codigo);
    }

    public boolean esActualizable(){
        return actualizable;
    }

    public boolean esEliminable(){
        return eliminable;
    }
}
