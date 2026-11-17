package com.rinconcito.marino.config;

import com.rinconcito.marino.model.Mesa;
import com.rinconcito.marino.repository.MesaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Crea 10 mesas de ejemplo la primera vez que arranca el sistema. */
@Configuration
public class MesaBootstrapConfig {

    @Bean
    CommandLineRunner crearMesasIniciales(MesaRepository mesaRepository) {
        return args -> {
            if (mesaRepository.count() > 0) {
                return;
            }
            for (int i = 1; i <= 10; i++) {
                Mesa m = new Mesa();
                m.setNumero(i);
                m.setCapacidad(i <= 6 ? 4 : 6);
                m.setZona(i <= 6 ? "Salón" : "Terraza");
                mesaRepository.save(m);
            }
        };
    }
}
