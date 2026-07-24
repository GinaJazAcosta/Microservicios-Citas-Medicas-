package com.gina.authorization.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import com.gina.authorization.dto.UsuarioRequest;
import com.gina.authorization.dto.UsuarioResponse;
import com.gina.authorization.entities.Rol;
import com.gina.authorization.entities.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse entityToResponse(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResponse(
                usuario.getUsername(),
                usuario.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.toSet())
        );
    }

    public Usuario requestToEntity(UsuarioRequest request, String password, Set<Rol> roles) {
        if (request == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(password);
        usuario.setRoles(roles);
        return usuario;
    }
}

