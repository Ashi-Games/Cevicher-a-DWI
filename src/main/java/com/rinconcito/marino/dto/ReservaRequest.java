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
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos numericos")
        String dni,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 18, message = "Debes ser mayor de edad para reservar")
        @Max(value = 120, message = "La edad ingresada no es valida")
        Integer edad,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo valido")
        @Size(max = 120, message = "El correo no debe superar 120 caracteres")
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
        String telefono,

        @NotNull(message = "La cantidad de personas es obligatoria")
        @Min(value = 1, message = "Debe reservar al menos una persona")
        @Max(value = 20, message = "Para mas de 20 personas contactanos por WhatsApp")
        Integer personas,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora
) {
}