package com.rinconcito.marino.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MesaRequest(
        @NotNull(message = "El numero de mesa es obligatorio")
        @Min(value = 1, message = "El numero de mesa debe ser mayor que cero")
        Integer numero,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser mayor que cero")
        @Max(value = 20, message = "La capacidad no debe superar 20 personas")
        Integer capacidad,

        @Size(max = 30, message = "La zona no debe superar 30 caracteres")
        String zona,

        Boolean activa
) {
}
