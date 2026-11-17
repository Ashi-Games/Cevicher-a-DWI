package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.*;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.exception.ReglaNegocioException;
import com.rinconcito.marino.model.*;
import com.rinconcito.marino.repository.*;
import com.rinconcito.marino.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/** Operaciones del personal sobre una reserva: mesas, adelanto, atencion e historial. */
@Service
@RequiredArgsConstructor
public class GestionReservaService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final List<EstadoReserva> ACTIVAS = List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO);

    private final ReservaRepository reservaRepository;
    private final MesaRepository mesaRepository;
    private final PagoAdelantoRepository pagoRepository;
    private final AtencionRepository atencionRepository;
    private final HistorialEstadoReservaRepository historialRepository;
    private final HistorialService historialService;
    private final UsuarioActual usuarioActual;

    @Transactional(readOnly = true)
    public ReservaDetalleResponse detalle(Long reservaId) {
        Reserva r = buscar(reservaId);
        return new ReservaDetalleResponse(
                ReservaResponse.from(r),
                r.getMesas().stream().sorted(Comparator.comparing(Mesa::getNumero)).map(MesaResponse::from).toList(),
                pagoRepository.findByReservaId(reservaId).map(PagoResponse::from).orElse(null),
                atencionRepository.findByReservaId(reservaId).map(AtencionResponse::from).orElse(null),
                historialRepository.findByReservaIdOrderByFechaCambioAsc(reservaId).stream()
                        .map(HistorialResponse::from).toList()
        );
    }

    @Transactional
    public ReservaDetalleResponse asignarMesas(Long reservaId, AsignarMesasRequest request) {
        Reserva r = buscar(reservaId);
        if (!ACTIVAS.contains(r.getEstado())) {
            throw new ReglaNegocioException("Solo se asignan mesas a reservas pendientes o confirmadas");
        }
        List<Mesa> mesas = mesaRepository.findAllById(request.mesaIds());
        if (mesas.size() != new HashSet<>(request.mesaIds()).size()) {
            throw new RecursoNoEncontradoException("Alguna de las mesas no existe");
        }
        int capacidad = 0;
        for (Mesa m : mesas) {
            if (!m.isActiva()) {
                throw new ReglaNegocioException("La mesa " + m.getNumero() + " no está activa");
            }
            if (reservaRepository.mesaOcupada(m.getId(), r.getFecha(), r.getHora(), ACTIVAS, r.getId())) {
                throw new ReglaNegocioException("La mesa " + m.getNumero() + " ya está asignada a esa fecha y hora");
            }
            capacidad += m.getCapacidad();
        }
        if (capacidad < r.getPersonas()) {
            throw new ReglaNegocioException("Las mesas elegidas suman " + capacidad
                    + " asientos y la reserva es para " + r.getPersonas());
        }
        r.setMesas(new HashSet<>(mesas));
        reservaRepository.save(r);
        return detalle(reservaId);
    }

    @Transactional
    public PagoResponse registrarPago(Long reservaId, PagoRequest request) {
        Reserva r = buscar(reservaId);
        PagoAdelanto pago = pagoRepository.findByReservaId(reservaId).orElseGet(PagoAdelanto::new);
        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new ReglaNegocioException("El adelanto de esta reserva ya fue pagado");
        }
        pago.setReserva(r);
        pago.setMetodo(request.metodo());
        pago.setMonto(request.monto() != null ? request.monto() : new BigDecimal("20.00"));
        pago.setReferencia(request.referencia());
        pago.setEstado(EstadoPago.PENDIENTE);
        return PagoResponse.from(pagoRepository.save(pago));
    }

    /** Al marcar el adelanto como PAGADO, la reserva pendiente se confirma automaticamente. */
    @Transactional
    public PagoResponse cambiarEstadoPago(Long reservaId, EstadoPago estado) {
        PagoAdelanto pago = pagoRepository.findByReservaId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no tiene adelanto registrado"));
        pago.setEstado(estado);
        pago.setFechaPago(estado == EstadoPago.PAGADO ? LocalDateTime.now(LIMA) : null);
        Reserva r = pago.getReserva();
        if (estado == EstadoPago.PAGADO && r.getEstado() == EstadoReserva.PENDIENTE) {
            r.setEstado(EstadoReserva.CONFIRMADO);
            reservaRepository.save(r);
            historialService.registrar(r, EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADO, "Adelanto pagado");
        }
        return PagoResponse.from(pagoRepository.save(pago));
    }

    @Transactional
    public AtencionResponse registrarLlegada(Long reservaId, AtencionRequest request) {
        Reserva r = buscar(reservaId);
        if (r.getEstado() != EstadoReserva.CONFIRMADO) {
            throw new ReglaNegocioException("Solo se atienden reservas confirmadas");
        }
        if (atencionRepository.findByReservaId(reservaId).isPresent()) {
            throw new ReglaNegocioException("La llegada de esta reserva ya fue registrada");
        }
        Atencion a = new Atencion();
        a.setReserva(r);
        a.setUsuario(usuarioActual.obtener()
                .orElseThrow(() -> new ReglaNegocioException("Usuario no identificado")));
        a.setHoraLlegada(LocalDateTime.now(LIMA));
        a.setPersonasAtendidas(request.personasAtendidas() != null ? request.personasAtendidas() : r.getPersonas());
        a.setObservaciones(request.observaciones());
        return AtencionResponse.from(atencionRepository.save(a));
    }

    @Transactional
    public AtencionResponse registrarSalida(Long reservaId) {
        Atencion a = atencionRepository.findByReservaId(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Primero registra la llegada"));
        if (a.getHoraSalida() != null) {
            throw new ReglaNegocioException("La salida ya fue registrada");
        }
        a.setHoraSalida(LocalDateTime.now(LIMA));
        return AtencionResponse.from(atencionRepository.save(a));
    }

    private Reserva buscar(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + id));
    }
}
