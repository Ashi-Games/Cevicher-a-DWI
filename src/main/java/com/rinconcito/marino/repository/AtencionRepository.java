package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    Optional<Atencion> findByReservaId(Long reservaId);
}