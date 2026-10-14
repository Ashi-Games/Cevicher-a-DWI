package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.RolUsuario;

import java.time.Instant;

public record AuthTokenResponse(
        String token,
        String tipo,
        String usuario,
        RolUsuario rol,
        Instant expiraEn
) {
}