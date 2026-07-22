package com.gina.medicos.repository;

import com.gina.common.enums.EstadoRegistro;
import com.gina.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    //ENCONTRAR UNA LISTA DE MEDICOS POR UN ESTADO
    List<Medico> findByEstadoRegistro(EstadoRegistro estadoRegistro);
    //ENCONTRAR UN MEDICO POR UN ESTADO
    Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
    //VALIDAR SI EXISTE UN EMAIL, TELEFONO, CEDULA PROFESIONAL POR UN ESTADO DE REGISTRO
    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);
    boolean existsByTelefonoIgnoreCaseAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);
    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(String cedulaProfesional, EstadoRegistro estadoRegistro);
    //VALIDAR SI EXISTE UN EMAIL, TELEFONO, CEDULA PROFESIONAL POR UN ESTADO DE REGISTRO PARA ACTUALIZAR
    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(String email, EstadoRegistro estadoRegistro, Long id);
    boolean existsByTelefonoIgnoreCaseAndEstadoRegistroAndIdNot(String telefono, EstadoRegistro estadoRegistro, Long id);
    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(String cedulaProfesional, EstadoRegistro estadoRegistro, Long id);
}
