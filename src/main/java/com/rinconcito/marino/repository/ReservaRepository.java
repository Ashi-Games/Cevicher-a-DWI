package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    long countByEstado(EstadoReserva estado);

    long countByFechaAndEstado(LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByEstadoAndFechaRegistroBefore(EstadoReserva estado, LocalDateTime fechaRegistro);
}