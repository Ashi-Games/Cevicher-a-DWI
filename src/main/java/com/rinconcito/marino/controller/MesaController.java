package com.rinconcito.marino.controller;

import com.rinconcito.marino.dto.MesaRequest;
import com.rinconcito.marino.dto.MesaResponse;
import com.rinconcito.marino.service.MesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public List<MesaResponse> listar() {
        return mesaService.listar();
    }

    @PostMapping
    public ResponseEntity<MesaResponse> crear(@Valid @RequestBody MesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.crear(request));
    }

    @PutMapping("/{id}")
    public MesaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MesaRequest request) {
        return mesaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
