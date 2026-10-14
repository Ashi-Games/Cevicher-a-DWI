package com.rinconcito.marino.security;

import com.rinconcito.marino.model.UsuarioAdmin;
import com.rinconcito.marino.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final UsuarioAdminRepository usuarioAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioAdmin usuario = usuarioAdminRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getPassword())
                .authorities(usuario.getRol().getAuthority())
                .build();
    }
}