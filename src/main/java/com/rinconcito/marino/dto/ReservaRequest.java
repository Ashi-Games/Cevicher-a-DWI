package com.rinconcito.marino.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 80, message = "Los nombres no deben superar 80 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80, message = "Los apellidos no deben superar 80 caracteres")
        String apellidos,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos numéricos")
        String dni,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 18, message = "Debes ser mayor de edad para reservar")
        @Max(value = 120, message = "La edad ingresada no es válida")
        Integer edad,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo válido")
        @Size(max = 120, message = "El correo no debe superar 120 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
        String telefono,

        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "Debe reservar al menos una persona")
        @Max(value = 20, message = "Para más de 20 personas contáctanos por WhatsApp")
        Integer personas,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora
) {
}