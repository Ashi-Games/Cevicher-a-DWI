package com.rinconcito.marino.controller;

import com.rinconcito.marino.security.JwtAuthenticationFilter;
import com.rinconcito.marino.security.JwtUtil;

import com.rinconcito.marino.config.SecurityConfig;
import com.rinconcito.marino.dto.OpinionRequest;
import com.rinconcito.marino.dto.OpinionResponse;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.service.OpinionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OpinionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@WithMockUser(roles = "ADMIN")
class OpinionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpinionService opinionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private OpinionResponse opinionResponse() {
        return new OpinionResponse(1L, "Eidan", "eidan@test.com", "Muy rico el ceviche",
                LocalDateTime.of(2026, 10, 14, 12, 0));
    }

    @Test
    void post_conDatosValidos_devuelve201() throws Exception {
        when(opinionService.guardar(any(OpinionRequest.class))).thenReturn(opinionResponse());

        mockMvc.perform(post("/api/opiniones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombres":"Eidan","correo":"eidan@test.com","comentario":"Muy rico el ceviche"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombres").value("Eidan"));
    }

    @Test
    void post_conDatosInvalidos_devuelve400ConErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/opiniones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombres":"","correo":"malo","comentario":"hi"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos invalidos"))
                .andExpect(jsonPath("$.errores.nombres").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.comentario").exists());

        verifyNoInteractions(opinionService);
    }

    @Test
    void get_devuelveLaLista() throws Exception {
        when(opinionService.listar(null)).thenReturn(List.of(opinionResponse()));

        mockMvc.perform(get("/api/opiniones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].comentario").value("Muy rico el ceviche"));
    }

    @Test
    void delete_existente_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/opiniones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_inexistente_devuelve404ConElFormatoDeError() throws Exception {
        doThrow(new RecursoNoEncontradoException("Opinion no encontrada: 99"))
                .when(opinionService).eliminar(99L);

        mockMvc.perform(delete("/api/opiniones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Opinion no encontrada: 99"));
    }
}