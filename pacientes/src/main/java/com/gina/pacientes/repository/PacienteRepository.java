package com.gina.pacientes.repository;

import com.gina.common.enums.EstadoRegistro;
import com.gina.pacientes.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    //ENCONTRAR UNA LISTA DE PACIENTES POR UN ESTADO
    List<Paciente> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    //ENCONTRAR UN PACIENTE POR UN ESTADO
    Optional<Paciente> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
    //VALIDAR SI EXISTE UN EMAIL, TELEFONO POR UN ESTADO DE REGISTRO
    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);
    boolean existsByTelefonoIgnoreCaseAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);
    //VALIDAR SI EXISTE UN EMAIL, TELEFONO POR UN ESTADO DE REGISTRO PARA ACTUALIZAR
    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(String email, EstadoRegistro estadoRegistro, Long id);
    boolean existsByTelefonoIgnoreCaseAndEstadoRegistroAndIdNot(String telefono, EstadoRegistro estadoRegistro, Long id);
}
