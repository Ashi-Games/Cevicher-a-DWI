package com.rinconcito.marino.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CONFIRMADO("Confirmado"),
    RECHAZADO("Rechazado"),
    EXPIRADO("Expirado");

    private final String etiqueta;
}