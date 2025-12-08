// src/main/java/com/nautica/backend/nautica_ies_backend/services/CuotaScheduler.java
package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.repository.CuotaRepository;

@Service
public class CuotaScheduler {

    private static final Logger log = LoggerFactory.getLogger(CuotaScheduler.class);

    private final CuotaService cuotaService;
    private final CuotaRepository cuotaRepo;

    public CuotaScheduler(CuotaService cuotaService, CuotaRepository cuotaRepo) {
        this.cuotaService = cuotaService;
        this.cuotaRepo = cuotaRepo;
    }

   /**
     * Se ejecuta automáticamente el día 1 de cada mes a las 03:00.
     *
     * 1) Marca como "vencida" todas las cuotas de meses anteriores
     *    que sigan en "pendiente".
     * 2) Genera las cuotas del mes actual (solo propietarios, según
     *    la lógica de CuotaService.generarCuotasMes).
     */
    // @Scheduled(cron = "*/10 * * * * *")
    
    @Scheduled(cron = "0 0 3 1 * ?")
    @Transactional
    public void procesarCuotasMes() {
        LocalDate hoy = LocalDate.now();
        LocalDate mesActual = hoy.withDayOfMonth(1); // ej: 2025-12-01

        // 1) Marcar cuotas viejas pendientes como vencidas
        try {
            int actualizadas = cuotaRepo.actualizarEstadoCuotasAntesDe(
                    mesActual,
                    EstadoCuota.pendiente,
                    EstadoCuota.vencida
            );
            log.info("Cuotas pendientes de meses anteriores marcadas como vencidas: {}", actualizadas);
        } catch (Exception e) {
            log.error("Error marcando cuotas pendientes como vencidas antes de {}: {}", mesActual, e.getMessage(), e);
        }

        // // 2) Generar cuotas del mes actual si todavía no existen
        // if (cuotaRepo.existsByNumeroMes(mesActual)) {
        //     log.info("Cuotas del mes {} ya existen, no se generan nuevamente", mesActual);
        //     return;
        // }

        try {
            int creadas = cuotaService.generarCuotasMes(mesActual);
            log.info("Cuotas generadas automáticamente para {}: {} nuevas", mesActual, creadas);
        } catch (Exception e) {
            log.error("Error generando cuotas para el mes {}: {}", mesActual, e.getMessage(), e);
        }
    }
}
