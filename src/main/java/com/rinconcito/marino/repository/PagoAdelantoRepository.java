package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.PagoAdelanto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoAdelantoRepository extends JpaRepository<PagoAdelanto, Long> {

    Optional<PagoAdelanto> findByReservaId(Long reservaId);
}