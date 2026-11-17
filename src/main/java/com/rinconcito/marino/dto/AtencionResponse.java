package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.Atencion;

import java.time.LocalDateTime;

public record AtencionResponse(Long id, String atendidoPor, LocalDateTime horaLlegada,
                               LocalDateTime horaSalida, Integer personasAtendidas, String observaciones) {
    public static AtencionResponse from(Atencion a) {
        return new AtencionResponse(a.getId(), a.getUsuario().getUsuario(), a.getHoraLlegada(),
                a.getHoraSalida(), a.getPersonasAtendidas(), a.getObservaciones());
    }
}
