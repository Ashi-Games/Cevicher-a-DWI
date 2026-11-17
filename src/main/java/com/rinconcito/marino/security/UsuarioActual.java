package com.rinconcito.marino.security;

import com.rinconcito.marino.model.UsuarioAdmin;
import com.rinconcito.marino.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Obtiene el usuario autenticado (por JWT) que realiza la operacion. */
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioAdminRepository usuarioAdminRepository;

    public Optional<UsuarioAdmin> obtener() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return usuarioAdminRepository.findByUsuario(auth.getName());
    }
}
