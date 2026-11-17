package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.Mesa;

public record MesaResponse(Long id, Integer numero, Integer capacidad, String zona, boolean activa) {
    public static MesaResponse from(Mesa m) {
        return new MesaResponse(m.getId(), m.getNumero(), m.getCapacidad(), m.getZona(), m.isActiva());
    }
}
