package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.EstadoReserva;
import com.rinconcito.marino.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;
import java.util.Collection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    long countByEstado(EstadoReserva estado);

    long countByFechaAndEstado(LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByEstadoAndFechaRegistroBefore(EstadoReserva estado, LocalDateTime fechaRegistro);

    @Query("""
            select case when count(r) > 0 then true else false end from Reserva r join r.mesas m
            where m.id = :mesaId and r.fecha = :fecha and r.hora = :hora
              and r.estado in :estados and r.id <> :reservaId
            """)
    boolean mesaOcupada(@Param("mesaId") Long mesaId, @Param("fecha") LocalDate fecha,
                        @Param("hora") LocalTime hora, @Param("estados") Collection<EstadoReserva> estados,
                        @Param("reservaId") Long reservaId);
}
