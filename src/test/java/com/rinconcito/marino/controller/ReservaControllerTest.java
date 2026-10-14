package com.rinconcito.marino.controller;

import com.rinconcito.marino.config.SecurityConfig;
import com.rinconcito.marino.dto.ReservaCreadaResponse;
import com.rinconcito.marino.dto.ReservaRequest;
import com.rinconcito.marino.dto.ReservaResponse;
import com.rinconcito.marino.dto.ReservaStats;
import com.rinconcito.marino.exception.ReglaNegocioException;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@Import(SecurityConfig.class)
class ReservaControllerTest {

    private static final String RESERVA_VALIDA = """
            {"nombres":"Eidan","apellidos":"Prueba","dni":"12345678","edad":25,
             "email":"prueba@test.com","telefono":"987654321","personas":4,
             "fecha":"2026-12-20","hora":"19:30"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    private ReservaResponse reservaResponse(EstadoReserva estado) {
        return new ReservaResponse(1L, "Eidan", "Prueba", "12345678", 25, "prueba@test.com",
                "987654321", 4, LocalDate.of(2026, 12, 20), LocalTime.of(19, 30), estado,
                LocalDateTime.of(2026, 10, 14, 12, 0));
    }

    @Test
    void post_conDatosValidos_devuelve201ConReservaYUrlDeWhatsapp() throws Exception {
        when(reservaService.crear(any(ReservaRequest.class))).thenReturn(
                new ReservaCreadaResponse(reservaResponse(EstadoReserva.PENDIENTE), "https://wa.me/51999999999?text=hola"));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESERVA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reserva.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.whatsappUrl").value("https://wa.me/51999999999?text=hola"));
    }

    @Test
    void post_conDniInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESERVA_VALIDA.replace("12345678", "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.dni").exists());
    }

    @Test
    void post_conFechaPasada_devuelve400ConElMensajeDeLaRegla() throws Exception {
        when(reservaService.crear(any(ReservaRequest.class)))
                .thenThrow(new ReglaNegocioException("La fecha y hora de la reserva ya pasaron"));

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESERVA_VALIDA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La fecha y hora de la reserva ya pasaron"));
    }

    @Test
    void get_porIdInexistente_devuelve404() throws Exception {
        when(reservaService.obtenerPorId(999L))
                .thenThrow(new RecursoNoEncontradoException("Reserva no encontrada: 999"));

        mockMvc.perform(get("/api/reservas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Reserva no encontrada: 999"));
    }

    @Test
    void get_conIdQueNoEsNumero_devuelve400() throws Exception {
        mockMvc.perform(get("/api/reservas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void patch_estado_conValorValido_devuelve200() throws Exception {
        when(reservaService.cambiarEstado(eq(1L), eq(EstadoReserva.CONFIRMADO)))
                .thenReturn(reservaResponse(EstadoReserva.CONFIRMADO));

        mockMvc.perform(patch("/api/reservas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CONFIRMADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void patch_estado_conValorInexistente_devuelve400() throws Exception {
        mockMvc.perform(patch("/api/reservas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"XXX\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    void get_stats_devuelveLosContadores() throws Exception {
        when(reservaService.calcularStats()).thenReturn(new ReservaStats(3, 2, 5));

        mockMvc.perform(get("/api/reservas/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendientes").value(3))
                .andExpect(jsonPath("$.confirmadasHoy").value(2))
                .andExpect(jsonPath("$.opiniones").value(5));
    }
}