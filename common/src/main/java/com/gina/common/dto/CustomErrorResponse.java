package com.gina.common.dto;

public record CustomErrorResponse(
    int codigo,
    String mensaje
) { }
