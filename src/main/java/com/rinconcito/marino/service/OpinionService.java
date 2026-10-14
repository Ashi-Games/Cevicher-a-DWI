package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.OpinionRequest;
import com.rinconcito.marino.dto.OpinionResponse;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.model.Opinion;
import com.rinconcito.marino.repository.OpinionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OpinionService {

    private final OpinionRepository opinionRepository;

    @Transactional
    public OpinionResponse guardar(OpinionRequest request) {
        Opinion opinion = new Opinion();
        opinion.setNombres(request.nombres());
        opinion.setCorreo(request.correo());
        opinion.setComentario(request.comentario());
        return OpinionResponse.from(opinionRepository.save(opinion));
    }

    @Transactional(readOnly = true)
    public List<OpinionResponse> listar(String busqueda) {
        return opinionRepository
                .findAll(specBusqueda(busqueda), Sort.by(Sort.Direction.DESC, "fechaRegistro"))
                .stream()
                .map(OpinionResponse::from)
                .toList();
    }

    @Transactional
    public void eliminar(Long id) {
        if (!opinionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Opinion no encontrada: " + id);
        }
        opinionRepository.deleteById(id);
    }

    private Specification<Opinion> specBusqueda(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String termino = "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> {
            Predicate nombres = cb.like(cb.lower(root.get("nombres")), termino);
            Predicate correo = cb.like(cb.lower(root.get("correo")), termino);
            Predicate comentario = cb.like(cb.lower(root.get("comentario")), termino);
            return cb.or(nombres, correo, comentario);
        };
    }
}