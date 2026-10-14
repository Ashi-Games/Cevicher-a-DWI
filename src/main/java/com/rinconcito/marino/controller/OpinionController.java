package com.rinconcito.marino.controller;

import com.rinconcito.marino.dto.OpinionRequest;
import com.rinconcito.marino.dto.OpinionResponse;
import com.rinconcito.marino.service.OpinionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opiniones")
@RequiredArgsConstructor
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping
    public ResponseEntity<OpinionResponse> guardar(@Valid @RequestBody OpinionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(opinionService.guardar(request));
    }

    @GetMapping
    public List<OpinionResponse> listar(@RequestParam(required = false) String q) {
        return opinionService.listar(q);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        opinionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}