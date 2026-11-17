package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.MesaRequest;
import com.rinconcito.marino.dto.MesaResponse;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.exception.ReglaNegocioException;
import com.rinconcito.marino.model.Mesa;
import com.rinconcito.marino.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    @Transactional(readOnly = true)
    public List<MesaResponse> listar() {
        return mesaRepository.findAll(Sort.by("numero")).stream().map(MesaResponse::from).toList();
    }

    @Transactional
    public MesaResponse crear(MesaRequest request) {
        mesaRepository.findByNumero(request.numero()).ifPresent(m -> {
            throw new ReglaNegocioException("Ya existe la mesa " + request.numero());
        });
        Mesa mesa = new Mesa();
        aplicar(mesa, request);
        return MesaResponse.from(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaResponse actualizar(Long id, MesaRequest request) {
        Mesa mesa = buscar(id);
        mesaRepository.findByNumero(request.numero())
                .filter(otra -> !otra.getId().equals(id))
                .ifPresent(m -> {
                    throw new ReglaNegocioException("Ya existe la mesa " + request.numero());
                });
        aplicar(mesa, request);
        return MesaResponse.from(mesaRepository.save(mesa));
    }

    @Transactional
    public void eliminar(Long id) {
        mesaRepository.delete(buscar(id));
    }

    Mesa buscar(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada: " + id));
    }

    private void aplicar(Mesa mesa, MesaRequest r) {
        mesa.setNumero(r.numero());
        mesa.setCapacidad(r.capacidad());
        mesa.setZona(r.zona());
        mesa.setActiva(r.activa() == null || r.activa());
    }
}
