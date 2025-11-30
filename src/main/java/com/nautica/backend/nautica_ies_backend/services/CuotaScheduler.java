// src/main/java/com/nautica/backend/nautica_ies_backend/services/CuotaScheduler.java
package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * - Toma la fecha actual del servidor.
     * - Normaliza al día 1.
     * - Si ya existen cuotas para ese numeroMes -> no hace nada.
     * - Si no existen -> llama a generarCuotasMes(mes).
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    @Transactional
    public void generarCuotasMesActualSiFalta() {
        LocalDate hoy = LocalDate.now();
        LocalDate mes = hoy.withDayOfMonth(1);

        if (cuotaRepo.existsByNumeroMes(mes)) {
            log.info("Cuotas del mes {} ya existen, no se generan nuevamente", mes);
            return;
        }

        try {
            int creadas = cuotaService.generarCuotasMes(mes);
            log.info("Cuotas generadas automáticamente para {}: {} nuevas", mes, creadas);
        } catch (Exception e) {
            log.error("Error generando cuotas para el mes {}: {}", mes, e.getMessage(), e);
        }
    }
}
