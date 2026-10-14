package com.rinconcito.marino.service;

import com.rinconcito.marino.dto.OpinionRequest;
import com.rinconcito.marino.dto.OpinionResponse;
import com.rinconcito.marino.exception.RecursoNoEncontradoException;
import com.rinconcito.marino.model.Opinion;
import com.rinconcito.marino.repository.OpinionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpinionServiceTest {

    @Mock
    private OpinionRepository opinionRepository;

    @InjectMocks
    private OpinionService opinionService;

    @Test
    void guardar_devuelveLaOpinionConLosDatosEnviados() {
        OpinionRequest request = new OpinionRequest("Eidan", "eidan@test.com", "Muy rico el ceviche");
        when(opinionRepository.save(any(Opinion.class))).thenAnswer(inv -> {
            Opinion opinion = inv.getArgument(0);
            opinion.setId(1L);
            return opinion;
        });

        OpinionResponse response = opinionService.guardar(request);

        assertEquals(1L, response.id());
        assertEquals("Eidan", response.nombres());
        assertEquals("Muy rico el ceviche", response.comentario());
    }

    @Test
    void eliminar_borraLaOpinionSiExiste() {
        when(opinionRepository.existsById(1L)).thenReturn(true);

        opinionService.eliminar(1L);

        verify(opinionRepository).deleteById(1L);
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(opinionRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> opinionService.eliminar(99L));
        verify(opinionRepository, never()).deleteById(any());
    }
}