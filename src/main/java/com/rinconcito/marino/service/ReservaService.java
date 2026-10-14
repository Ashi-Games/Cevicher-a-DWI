package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.*;
import com.rinconcito.marino.exception.ReglaNegocioException;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.Reserva;
import com.rinconcito.marino.repository.OpinionRepository;
import com.rinconcito.marino.repository.ReservaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");
    private static final BigDecimal ADELANTO = new BigDecimal("20.00");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final Comparator<Reserva> ORDEN = Comparator
            .comparingInt((Reserva r) -> r.getEstado().getOrden())
            .thenComparing(Reserva::getFecha, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Reserva::getHora, Comparator.nullsLast(Comparator.naturalOrder()));

    private final ReservaRepository reservaRepository;
    private final OpinionRepository opinionRepository;

    @Value("${app.restaurant.whatsapp-number:51910872665}")
    private String whatsappNumber;

    @Transactional
    public ReservaCreadaResponse crear(ReservaRequest request) {
        if (LocalDateTime.of(request.fecha(), request.hora()).isBefore(LocalDateTime.now(LIMA_ZONE))) {
            throw new ReglaNegocioException("La fecha y hora de la reserva ya pasaron");
        }
        Reserva reserva = new Reserva();
        reserva.setNombres(request.nombres());
        reserva.setApellidos(request.apellidos());
        reserva.setDni(request.dni());
        reserva.setEdad(request.edad());
        reserva.setEmail(request.email());
        reserva.setTelefono(request.telefono());
        reserva.setPersonas(request.personas());
        reserva.setFecha(request.fecha());
        reserva.setHora(request.hora());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva guardada = reservaRepository.save(reserva);
        return new ReservaCreadaResponse(ReservaResponse.from(guardada), generarWhatsappUrl(guardada));
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id) {
        return ReservaResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listar(String busqueda) {
        return reservaRepository.findAll(specBusqueda(busqueda)).stream()
                .sorted(ORDEN)
                .map(ReservaResponse::from)
                .toList();
    }

    @Transactional
    public ReservaResponse cambiarEstado(Long id, EstadoReserva estado) {
        Reserva reserva = buscar(id);
        if (reserva.getEstado() == EstadoReserva.EXPIRADO && estado == EstadoReserva.CONFIRMADO) {
            throw new ReglaNegocioException("Una reserva expirada no puede confirmarse");
        }
        reserva.setEstado(estado);
        return ReservaResponse.from(reservaRepository.save(reserva));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Reserva no encontrada: " + id);
        }
        reservaRepository.deleteById(id);
    }

    @Transactional
    public int expirarPendientesVencidas() {
        LocalDateTime limite = LocalDateTime.now(LIMA_ZONE).minusMinutes(30);
        List<Reserva> vencidas = reservaRepository
                .findByEstadoAndFechaRegistroBefore(EstadoReserva.PENDIENTE, limite);
        vencidas.forEach(r -> r.setEstado(EstadoReserva.EXPIRADO));
        reservaRepository.saveAll(vencidas);
        return vencidas.size();
    }

    @Transactional(readOnly = true)
    public ReservaStats calcularStats() {
        LocalDate hoy = LocalDate.now(LIMA_ZONE);
        return new ReservaStats(
                reservaRepository.countByEstado(EstadoReserva.PENDIENTE),
                reservaRepository.countByFechaAndEstado(hoy, EstadoReserva.CONFIRMADO),
                opinionRepository.count()
        );
    }

    private Reserva buscar(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada: " + id));
    }

    private String generarWhatsappUrl(Reserva r) {
        String mensaje = "Hola " + (r.getNombres() + " " + r.getApellidos()).trim()
                + ", tu reserva en Rinconcito Marino para " + r.getPersonas()
                + " persona(s) el " + r.getFecha().format(FORMATO_FECHA)
                + " a las " + r.getHora().format(FORMATO_HORA)
                + " esta pendiente. Para confirmarla, coordina el adelanto de S/ " + ADELANTO + ".";
        return "https://wa.me/" + whatsappNumber + "?text="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
    }

    private Specification<Reserva> specBusqueda(String busqueda) {
        if (busqueda == null || busqueda.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String termino = "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> {
            Predicate nombres = cb.like(cb.lower(root.get("nombres")), termino);
            Predicate apellidos = cb.like(cb.lower(root.get("apellidos")), termino);
            Predicate dni = cb.like(cb.lower(root.get("dni")), termino);
            Predicate telefono = cb.like(cb.lower(root.get("telefono")), termino);
            Predicate email = cb.like(cb.lower(root.get("email")), termino);
            return cb.or(nombres, apellidos, dni, telefono, email);
        };
    }
}