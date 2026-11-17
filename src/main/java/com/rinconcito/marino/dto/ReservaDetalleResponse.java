package com.rinconcito.marino.dto;

import java.util.List;

public record ReservaDetalleResponse(
        ReservaResponse reserva,
        List<MesaResponse> mesas,
        PagoResponse pago,
        AtencionResponse atencion,
        List<HistorialResponse> historial
) {
}
