package com.rinconcito.marino.dto;

import com.rinconcito.marino.model.Opinion;

import java.time.LocalDateTime;

public record OpinionResponse(
        Long id,
        String nombres,
        String correo,
        String comentario,
        LocalDateTime fechaRegistro
) {
    public static OpinionResponse from(Opinion opinion) {
        return new OpinionResponse(
                opinion.getId(),
                opinion.getNombres(),
                opinion.getCorreo(),
                opinion.getComentario(),
                opinion.getFechaRegistro()
        );
    }
}