package com.rinconcito.marino.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolUsuario {
    ADMIN("Administrador"),
    EMPLEADO("Empleado");

    private final String etiqueta;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}