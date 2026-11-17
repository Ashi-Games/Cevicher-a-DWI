package com.rinconcito.marino.controller;

import com.rinconcito.marino.dto.*;
import com.rinconcito.marino.service.GestionReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas/{id}")
@RequiredArgsConstructor
public class GestionReservaController {

    private final GestionReservaService service;

    @GetMapping("/detalle")
    public ReservaDetalleResponse detalle(@PathVariable Long id) {
        return service.detalle(id);
    }

    @PutMapping("/mesas")
    public ReservaDetalleResponse asignarMesas(@PathVariable Long id,
                                              @Valid @RequestBody AsignarMesasRequest request) {
        return service.asignarMesas(id, request);
    }

    @PostMapping("/pago")
    public ResponseEntity<PagoResponse> registrarPago(@PathVariable Long id,
                                                      @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarPago(id, request));
    }

    @PatchMapping("/pago/estado")
    public PagoResponse cambiarEstadoPago(@PathVariable Long id,
                                          @Valid @RequestBody CambioEstadoPagoRequest request) {
        return service.cambiarEstadoPago(id, request.estado());
    }

    @PostMapping("/atencion")
    public ResponseEntity<AtencionResponse> registrarLlegada(@PathVariable Long id,
                                                             @Valid @RequestBody AtencionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarLlegada(id, request));
    }

    @PatchMapping("/atencion/salida")
    public AtencionResponse registrarSalida(@PathVariable Long id) {
        return service.registrarSalida(id);
    }
}
