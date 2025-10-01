// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/DashboardController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.controllers.dto.DashboardDTO;
import com.nautica.backend.nautica_ies_backend.services.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
// @CrossOrigin(origins = {"http://localhost:5173"}) // habilitá CORS si lo necesitás
public class DashboardController {

  private final DashboardService service;

  public DashboardController(DashboardService service) {
    this.service = service;
  }

  /**
   * Devuelve los KPIs del dashboard.
   * Si no se envía ?fecha=YYYY-MM-DD, usa la fecha de hoy (America/Argentina/Cordoba).
   */
  @GetMapping("/kpis")
  public DashboardDTO kpis(
      @RequestParam(value = "fecha", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
  ) {
    if (fecha == null) {
      fecha = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
    }
    return service.kpis(fecha);
  }

  /**
   * Atajo para “hoy”.
   */
  @GetMapping("/kpis/hoy")
  public DashboardDTO kpisHoy() {
    LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
    return service.kpis(hoy);
  }
}
