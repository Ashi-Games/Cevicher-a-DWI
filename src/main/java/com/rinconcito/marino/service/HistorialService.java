package com.rinconcito.marino.service;

import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.HistorialEstadoReserva;
import com.rinconcito.marino.model.Reserva;
import com.rinconcito.marino.repository.HistorialEstadoReservaRepository;
import com.rinconcito.marino.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Registra cada cambio de estado de una reserva (auditoria). */
@Service
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialEstadoReservaRepository historialRepository;
    private final UsuarioActual usuarioActual;

    public void registrar(Reserva reserva, EstadoReserva anterior, EstadoReserva nuevo, String motivo) {
        if (anterior == nuevo) {
            return;
        }
        HistorialEstadoReserva h = new HistorialEstadoReserva();
        h.setReserva(reserva);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setMotivo(motivo);
        h.setUsuario(usuarioActual.obtener().orElse(null));
        historialRepository.save(h);
    }
}
