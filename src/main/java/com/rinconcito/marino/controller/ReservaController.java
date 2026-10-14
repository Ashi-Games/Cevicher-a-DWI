package com.rinconcito.marino.controller;

import com.rinconcito.marino.dto.*;
import com.rinconcito.marino.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaCreadaResponse> crear(@Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request));
    }

    @GetMapping
    public List<ReservaResponse> listar(@RequestParam(required = false) String q) {
        return reservaService.listar(q);
    }

    @GetMapping("/stats")
    public ReservaStats stats() {
        return reservaService.calcularStats();
    }

    @GetMapping("/{id}")
    public ReservaResponse obtener(@PathVariable Long id) {
        return reservaService.obtenerPorId(id);
    }

    @PatchMapping("/{id}/estado")
    public ReservaResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody CambioEstadoRequest request) {
        return reservaService.cambiarEstado(id, request.estado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}