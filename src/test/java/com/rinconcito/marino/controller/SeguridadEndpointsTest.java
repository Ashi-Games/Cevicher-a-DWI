package com.rinconcito.marino.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rinconcito.marino.config.SecurityConfig;
import com.rinconcito.marino.dto.LoginRequest;
import com.rinconcito.marino.security.AuthenticationService;
import com.rinconcito.marino.security.JwtAuthenticationFilter;
import com.rinconcito.marino.security.JwtUtil;
import com.rinconcito.marino.service.OpinionService;
import com.rinconcito.marino.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OpinionController.class, ReservaController.class, AuthController.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SeguridadEndpointsTest {

    private static final String OPINION_VALIDA = """
            {"nombres":"Eidan","correo":"eidan@test.com","comentario":"Muy rico el ceviche"}
            """;

    private static final String RESERVA_VALIDA = """
            {"nombres":"Eidan","apellidos":"Prueba","dni":"12345678","edad":25,
             "email":"prueba@test.com","telefono":"987654321","personas":4,
             "fecha":"2026-12-20","hora":"19:30"}
            """;

    private static final String LOGIN = """
            {"usuario":"admin","password":"clave-de-prueba"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpinionService opinionService;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // ---------- Sin sesion ----------

    @Test
    void anonimo_puedeCrearOpinion() throws Exception {
        mockMvc.perform(post("/api/opiniones").contentType(MediaType.APPLICATION_JSON).content(OPINION_VALIDA))
                .andExpect(status().isCreated());
    }

    @Test
    void anonimo_puedeCrearReserva() throws Exception {
        mockMvc.perform(post("/api/reservas").contentType(MediaType.APPLICATION_JSON).content(RESERVA_VALIDA))
                .andExpect(status().isCreated());
    }

    @Test
    void anonimo_noPuedeListarReservas() throws Exception {
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void anonimo_noPuedeListarOpiniones() throws Exception {
        mockMvc.perform(get("/api/opiniones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonimo_noPuedeCambiarEstado() throws Exception {
        mockMvc.perform(patch("/api/reservas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CONFIRMADO\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonimo_noPuedeBorrar() throws Exception {
        mockMvc.perform(delete("/api/opiniones/1"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(opinionService);
    }

    @Test
    void anonimo_enRutaInexistente_recibe401() throws Exception {
        mockMvc.perform(get("/api/no-existe"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- Login ----------

    @Test
    void login_esPublico() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN))
                .andExpect(status().isOk());
    }

    @Test
    void login_conCredencialesInvalidas_devuelve401() throws Exception {
        when(authenticationService.autenticar(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciales invalidas"));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales invalidas"));
    }

    @Test
    void login_conCamposVacios_devuelve400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.usuario").exists())
                .andExpect(jsonPath("$.errores.password").exists());
    }

    // ---------- EMPLEADO ----------

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleado_puedeListarReservas() throws Exception {
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleado_puedeCambiarEstado() throws Exception {
        mockMvc.perform(patch("/api/reservas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CONFIRMADO\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleado_noPuedeBorrarReservas() throws Exception {
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        verifyNoInteractions(reservaService);
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleado_noPuedeBorrarOpiniones() throws Exception {
        mockMvc.perform(delete("/api/opiniones/1"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(opinionService);
    }

    // ---------- ADMIN ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeBorrarReservas() throws Exception {
        mockMvc.perform(delete("/api/reservas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeBorrarOpiniones() throws Exception {
        mockMvc.perform(delete("/api/opiniones/1"))
                .andExpect(status().isNoContent());
    }

    // ---------- Token en el header (prueba el filtro JWT) ----------

    @Test
    void conTokenValido_elFiltroAutenticaYDejaPasar() throws Exception {
        DecodedJWT jwt = mock(DecodedJWT.class);
        when(jwt.getSubject()).thenReturn("admin");
        when(jwtUtil.validar("token-bueno")).thenReturn(jwt);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(
                User.withUsername("admin").password("no-importa").authorities("ROLE_ADMIN").build());

        mockMvc.perform(get("/api/reservas").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isOk());
    }

    @Test
    void conTokenInvalido_devuelve401() throws Exception {
        when(jwtUtil.validar("token-falso")).thenThrow(new JWTVerificationException("invalido"));

        mockMvc.perform(get("/api/reservas").header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }
}