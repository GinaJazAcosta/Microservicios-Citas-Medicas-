package com.gina.citas.repository;

import com.gina.citas.entity.Cita;
import com.gina.citas.enums.EstadoCita;
import com.gina.common.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    //ENCONTRAR UNA LISTA DE PACIENTES POR UN ESTADO
    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    //ENCONTRAR UN PACIENTE POR UN ESTADO
    Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente,
            EstadoRegistro estadoRegistro,
            Collection<EstadoCita> estados);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico,
            EstadoRegistro estadoRegistro,
            Collection<EstadoCita> estados);

    boolean existsByIdPacienteAndIdNotAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente,
            Long idCita,
            EstadoRegistro estadoRegistro,
            Collection<EstadoCita> estados);

    boolean existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico,
            Long idCita,
            EstadoRegistro estadoRegistro,
            Collection<EstadoCita> estados);
}
