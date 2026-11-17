package com.rinconcito.marino.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AtencionRequest(
        @Min(value = 0, message = "Las personas atendidas no pueden ser negativas")
        Integer personasAtendidas,

        @Size(max = 300, message = "Las observaciones no deben superar 300 caracteres")
        String observaciones
) {
}
