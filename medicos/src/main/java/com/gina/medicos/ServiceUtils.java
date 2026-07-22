package com.gina.medicos;

import com.gina.common.enums.EstadoRegistro;
import com.gina.common.exceptions.RecursoNoEncontradoException;
import com.gina.medicos.entity.Medico;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Slf4j
public class ServiceUtils {

    public static <E, ID> E obtenerEntidadOException(
            JpaRepository<E, ID> repository,
            ID id,
            Class<E> clase
    ){
        String nombreEntidad = clase.getSimpleName();
        log.info("Buscando {} con id: {}", nombreEntidad, id);
        return repository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException(nombreEntidad + " no encontrado con id: " + id));
    }

}
