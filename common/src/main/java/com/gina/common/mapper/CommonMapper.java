package com.gina.common.mapper;

public interface CommonMapper  <RQ, RS, E> {
    E requestAEntidad(RQ request);
    RS entidadAResponse(E entidad);
}
