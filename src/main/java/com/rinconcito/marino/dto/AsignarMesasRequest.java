package com.rinconcito.marino.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AsignarMesasRequest(
        @NotEmpty(message = "Selecciona al menos una mesa")
        List<Long> mesaIds
) {
}
