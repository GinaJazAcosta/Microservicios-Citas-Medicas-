package com.gina.authorization.services;

import com.gina.authorization.dto.UsuarioRequest;
import com.gina.authorization.dto.UsuarioResponse;

import java.util.Set;

public interface UsuarioService {

    Set<UsuarioResponse> listar();

    UsuarioResponse registrar(UsuarioRequest request);

    UsuarioResponse eliminar(String username);
}
