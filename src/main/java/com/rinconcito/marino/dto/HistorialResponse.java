package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.HistorialEstadoReserva;

import java.time.LocalDateTime;

public record HistorialResponse(EstadoReserva estadoAnterior, EstadoReserva estadoNuevo,
                                String usuario, String motivo, LocalDateTime fechaCambio) {
    public static HistorialResponse from(HistorialEstadoReserva h) {
        return new HistorialResponse(h.getEstadoAnterior(), h.getEstadoNuevo(),
                h.getUsuario() == null ? "Sistema" : h.getUsuario().getUsuario(),
                h.getMotivo(), h.getFechaCambio());
    }
}
