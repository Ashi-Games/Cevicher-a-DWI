package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull(message = "El metodo de pago es obligatorio")
        MetodoPago metodo,

        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto,

        @Size(max = 60, message = "La referencia no debe superar 60 caracteres")
        String referencia
) {
}
