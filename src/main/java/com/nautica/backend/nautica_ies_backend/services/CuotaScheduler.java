// src/main/java/com/nautica/backend/nautica_ies_backend/services/CuotaScheduler.java
package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.models.Cuota;
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

     /**
     * Se ejecuta TODOS LOS DÍAS a las 03:05.
     * - Si pasaron 10 días desde el inicio del mes -> +10% (una sola vez).
     * - Si pasó 1 mes completo -> +20% total (una sola vez).
     */
    @Scheduled(cron = "0 5 3 * * ?")
    @Transactional
    public void aplicarRecargosMora() {
        LocalDate hoy = LocalDate.now();

        var estadosImpagos = List.of(EstadoCuota.pendiente, EstadoCuota.vencida);
        var cuotasImpagas = cuotaRepo.findByEstadoCuotaIn(estadosImpagos);

        int count10 = 0;
        int count20 = 0;

        for (Cuota c : cuotasImpagas) {
            LocalDate mes = c.getNumeroMes();     // ej: 2025-12-01
            if (mes == null) continue;

            LocalDate diezDias = mes.plusDays(10);   // ajustar si querés otro día
            LocalDate unMes = mes.plusMonths(1);

            // nos aseguramos de tener montoOriginal
            if (c.getMontoOriginal() == null) {
                c.setMontoOriginal(c.getMonto());
            }
            if (c.getPorcentajeRecargo() == null) {
                c.setPorcentajeRecargo(0);
            }

            // 1) primero chequeo si ya pasó 1 mes: -> 20%
            if (!hoy.isBefore(unMes)) { // hoy >= unMes
                boolean aplicado = aplicarRecargo(c, 20);
                if (aplicado) count20++;
                continue;
            }

            // 2) si no pasó el mes, pero sí 10 días: -> 10%
            if (!hoy.isBefore(diezDias)) { // hoy >= diezDias
                boolean aplicado = aplicarRecargo(c, 10);
                if (aplicado) count10++;
            }
        }

        log.info("Recargos mora aplicados: +10% a {} cuotas, +20% total a {}", count10, count20);
    }

    /**
     * Aplica el recargo deseado (10 o 20) SOLO si todavía no se había aplicado
     * uno igual o mayor. Siempre recalcula desde montoOriginal (no compone).
     */
    private boolean aplicarRecargo(Cuota c, int porcentajeTarget) {
        Integer actual = c.getPorcentajeRecargo() != null ? c.getPorcentajeRecargo() : 0;

        // si ya tiene un recargo igual o mayor, no hacemos nada
        if (actual >= porcentajeTarget) {
            return false;
        }

        BigDecimal base = c.getMontoOriginal();
        if (base == null) {
            base = c.getMonto();
            c.setMontoOriginal(base);
        }

        BigDecimal factor;
        if (porcentajeTarget == 10) {
            factor = new BigDecimal("1.10");
        } else if (porcentajeTarget == 20) {
            factor = new BigDecimal("1.20");
        } else {
            // por las dudas, aunque en tu caso solo usamos 10 o 20
            factor = BigDecimal.ONE;
        }

        c.setMonto(base.multiply(factor));
        c.setPorcentajeRecargo(porcentajeTarget);
        return true;
    }
}
