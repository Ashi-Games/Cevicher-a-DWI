package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.HistorialEstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoReservaRepository extends JpaRepository<HistorialEstadoReserva, Long> {

    List<HistorialEstadoReserva> findByReservaIdOrderByFechaCambioAsc(Long reservaId);
}