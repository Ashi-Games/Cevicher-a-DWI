package com.rinconcito.marino.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EstadoReserva {
    PENDIENTE("Pendiente", 1),
    CONFIRMADO("Confirmado", 2),
    RECHAZADO("Rechazado", 3),
    EXPIRADO("Expirado", 4);

    private final String etiqueta;
    private final int orden;
}