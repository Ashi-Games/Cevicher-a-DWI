package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        String nombres,
        String apellidos,
        String dni,
        Integer edad,
        String email,
        String telefono,
        Integer personas,
        LocalDate fecha,
        LocalTime hora,
        EstadoReserva estado,
        LocalDateTime fechaRegistro
) {
    public static ReservaResponse from(Reserva r) {
        return new ReservaResponse(
                r.getId(), r.getNombres(), r.getApellidos(), r.getDni(), r.getEdad(),
                r.getEmail(), r.getTelefono(), r.getPersonas(), r.getFecha(), r.getHora(),
                r.getEstado(), r.getFechaRegistro()
        );
    }
}