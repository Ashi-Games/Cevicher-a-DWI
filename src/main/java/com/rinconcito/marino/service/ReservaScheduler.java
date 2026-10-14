package com.rinconcito.marino.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ReservaScheduler {

    private final ReservaService reservaService;

    @Scheduled(fixedDelay = 60_000)
    public void expirarPendientes() {
        int expiradas = reservaService.expirarPendientesVencidas();
        if (expiradas > 0) {
            log.info("Reservas pendientes expiradas: {}", expiradas);
        }
    }
}