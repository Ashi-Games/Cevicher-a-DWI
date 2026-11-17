package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.EstadoPago;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoPagoRequest(
        @NotNull(message = "El estado del pago es obligatorio")
        EstadoPago estado
) {
}
