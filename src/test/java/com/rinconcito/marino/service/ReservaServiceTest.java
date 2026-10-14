package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.ReservaCreadaResponse;
import com.rinconcito.marino.dto.ReservaRequest;
import com.rinconcito.marino.dto.ReservaResponse;
import com.rinconcito.marino.dto.ReservaStats;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.exception.ReglaNegocioException;
import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.Reserva;
import com.rinconcito.marino.repository.OpinionRepository;
import com.rinconcito.marino.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private OpinionRepository opinionRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservaService, "whatsappNumber", "51999999999");
    }

    private ReservaRequest request(LocalDate fecha) {
        return new ReservaRequest("Eidan", "Prueba", "12345678", 25, "prueba@test.com",
                "987654321", 4, fecha, LocalTime.of(19, 30));
    }

    private Reserva reserva(Long id, EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setEstado(estado);
        return reserva;
    }

    @Test
    void crear_conFechaFutura_guardaPendienteYDevuelveUrlDeWhatsapp() {
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva reserva = inv.getArgument(0);
            reserva.setId(1L);
            return reserva;
        });

        ReservaCreadaResponse response = reservaService.crear(request(LocalDate.now(LIMA).plusDays(1)));

        assertEquals(EstadoReserva.PENDIENTE, response.reserva().estado());
        assertTrue(response.whatsappUrl().startsWith("https://wa.me/51999999999?text="));
    }

    @Test
    void crear_conFechaPasada_lanzaReglaNegocioYNoGuarda() {
        assertThrows(ReglaNegocioException.class,
                () -> reservaService.crear(request(LocalDate.now(LIMA).minusDays(1))));

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_siNoExiste_lanzaNoEncontrado() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.obtenerPorId(99L));
    }

    @Test
    void cambiarEstado_actualizaElEstado() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse response = reservaService.cambiarEstado(1L, EstadoReserva.CONFIRMADO);

        assertEquals(EstadoReserva.CONFIRMADO, response.estado());
    }

    @Test
    void expirarPendientesVencidas_marcaTodasComoExpiradas() {
        List<Reserva> vencidas = List.of(
                reserva(1L, EstadoReserva.PENDIENTE),
                reserva(2L, EstadoReserva.PENDIENTE));
        when(reservaRepository.findByEstadoAndFechaRegistroBefore(
                eq(EstadoReserva.PENDIENTE), any(LocalDateTime.class))).thenReturn(vencidas);

        int expiradas = reservaService.expirarPendientesVencidas();

        assertEquals(2, expiradas);
        assertTrue(vencidas.stream().allMatch(r -> r.getEstado() == EstadoReserva.EXPIRADO));
        verify(reservaRepository).saveAll(vencidas);
    }

    @Test
    void calcularStats_devuelveLosTresContadores() {
        when(reservaRepository.countByEstado(EstadoReserva.PENDIENTE)).thenReturn(3L);
        when(reservaRepository.countByFechaAndEstado(any(LocalDate.class), eq(EstadoReserva.CONFIRMADO)))
                .thenReturn(2L);
        when(opinionRepository.count()).thenReturn(5L);

        ReservaStats stats = reservaService.calcularStats();

        assertEquals(3L, stats.pendientes());
        assertEquals(2L, stats.confirmadasHoy());
        assertEquals(5L, stats.opiniones());
    }
    @Test
    void cambiarEstado_aConfirmadoUnaReservaExpirada_lanzaReglaNegocio() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.EXPIRADO)));

        assertThrows(ReglaNegocioException.class,
                () -> reservaService.cambiarEstado(1L, EstadoReserva.CONFIRMADO));
        verify(reservaRepository, never()).save(any());
    }
}
