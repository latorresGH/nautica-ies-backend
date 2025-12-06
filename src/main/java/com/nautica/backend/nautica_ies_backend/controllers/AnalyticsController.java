package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Reportes.ReportesAdminDTO;
import com.nautica.backend.nautica_ies_backend.services.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Dashboard general admin.
     * Ejemplo de uso:
     * GET /api/analytics/admin?desde=2025-01-01&hasta=2025-01-31
     */
    @GetMapping("/admin")
    public ResponseEntity<ReportesAdminDTO> getAdminDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        ReportesAdminDTO dto = analyticsService.buildAdminDashboard(desde, hasta);
        return ResponseEntity.ok(dto);
    }
}
