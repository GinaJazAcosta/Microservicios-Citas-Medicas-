package com.gina.authorization.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) { }
