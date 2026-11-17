package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.EstadoPago;
import com.rinconcito.marino.model.MetodoPago;
import com.rinconcito.marino.model.PagoAdelanto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(Long id, BigDecimal monto, MetodoPago metodo, EstadoPago estado,
                           String referencia, LocalDateTime fechaPago) {
    public static PagoResponse from(PagoAdelanto p) {
        return new PagoResponse(p.getId(), p.getMonto(), p.getMetodo(), p.getEstado(),
                p.getReferencia(), p.getFechaPago());
    }
}
