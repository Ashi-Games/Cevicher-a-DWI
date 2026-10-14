package com.rinconcito.marino.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> errores
) {
    public static ApiError of(HttpStatus status, String mensaje, String path, Map<String, String> errores) {
        return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensaje, path, errores);
    }
}