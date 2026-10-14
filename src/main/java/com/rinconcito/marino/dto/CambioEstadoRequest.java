package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.EstadoReserva;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoReserva estado
) {
}