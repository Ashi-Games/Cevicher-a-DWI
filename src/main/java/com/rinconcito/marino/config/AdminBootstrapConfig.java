package com.rinconcito.marino.config;

import com.rinconcito.marino.model.RolUsuario;
import com.rinconcito.marino.model.UsuarioAdmin;
import com.rinconcito.marino.repository.UsuarioAdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class AdminBootstrapConfig {

    @Bean
    CommandLineRunner crearAdminInicial(
            UsuarioAdminRepository usuarioAdminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.bootstrap.user:}") String usuario,
            @Value("${app.admin.bootstrap.password:}") String password,
            @Value("${app.admin.bootstrap.role:ADMIN}") String rol
    ) {
        return args -> {
            if (usuario.isBlank() || password.isBlank()) {
                log.info("Admin inicial omitido: ADMIN_USER y ADMIN_PASSWORD no estan configurados.");
                return;
            }
            if (usuarioAdminRepository.findByUsuario(usuario.trim()).isPresent()) {
                log.info("Admin inicial omitido: el usuario '{}' ya existe.", usuario.trim());
                return;
            }

            UsuarioAdmin admin = new UsuarioAdmin();
            admin.setUsuario(usuario.trim());
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol(RolUsuario.valueOf(rol.trim().toUpperCase()));
            usuarioAdminRepository.save(admin);
            log.info("Usuario inicial '{}' creado con rol {}.", admin.getUsuario(), admin.getRol());
        };
    }
}