package com.rinconcito.marino.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rinconcito.marino.model.UsuarioAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String ISSUER = "rinconcito-marino";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMinutes;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:480}") long expirationMinutes
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.jwt.secret debe tener al menos 32 caracteres");
        }
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        this.expirationMinutes = expirationMinutes;
    }

    public String generarToken(UsuarioAdmin usuario, Instant expiraEn) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getUsuario())
                .withClaim("rol", usuario.getRol().name())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(expiraEn))
                .sign(algorithm);
    }

    public DecodedJWT validar(String token) {
        return verifier.verify(token);
    }

    public Instant calcularExpiracion() {
        return Instant.now().plus(Duration.ofMinutes(expirationMinutes));
    }
}