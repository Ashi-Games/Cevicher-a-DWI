package com.rinconcito.marino.security;

import com.rinconcito.marino.dto.AuthTokenResponse;
import com.rinconcito.marino.dto.LoginRequest;
import com.rinconcito.marino.model.UsuarioAdmin;
import com.rinconcito.marino.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioAdminRepository usuarioAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public AuthTokenResponse autenticar(LoginRequest request) {
        UsuarioAdmin usuario = usuarioAdminRepository.findByUsuario(request.usuario())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        Instant expiraEn = jwtUtil.calcularExpiracion();
        String token = jwtUtil.generarToken(usuario, expiraEn);
        return new AuthTokenResponse(token, "Bearer", usuario.getUsuario(), usuario.getRol(), expiraEn);
    }
}