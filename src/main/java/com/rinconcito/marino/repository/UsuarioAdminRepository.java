package com.rinconcito.marino.repository;

import com.rinconcito.marino.model.UsuarioAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdmin, Long> {

    Optional<UsuarioAdmin> findByUsuario(String usuario);
}