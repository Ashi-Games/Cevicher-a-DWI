package com.rinconcito.marino.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpinionRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
        String nombres,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo valido")
        @Size(max = 120, message = "El correo no debe superar 120 caracteres")
        String correo,

        @NotBlank(message = "El comentario es obligatorio")
        @Size(min = 5, max = 600, message = "El comentario debe tener entre 5 y 600 caracteres")
        String comentario
) {
}