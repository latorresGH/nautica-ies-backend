package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Calendario.*;
import com.nautica.backend.nautica_ies_backend.services.CalendarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/calendario")
public class CalendarioController {

    private final CalendarioService service;

    public CalendarioController(CalendarioService service) {
        this.service = service;
    }

    // (EXISTENTE) GET /api/calendario?from=YYYY-MM-DD&to=YYYY-MM-DD
    @GetMapping
    public ResponseEntity<List<CalendarioService.DiaDTO>> getCalendario(
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "mes", required = false) String mes // NUEVO: prioridad si viene
    ) {
        // si viene mes=YYYY-MM, usamos el formato del contrato nuevo
        if (mes != null && !mes.isBlank()) {
            YearMonth ym = YearMonth.parse(mes); // "2025-10"
            var out = service.calendarioDelMes(ym);
            return ResponseEntity.ok(out.stream().map(d ->
                    new CalendarioService.DiaDTO(d.fecha(),
                            d.disponible(),
                            d.franjas() != null && !d.franjas().isEmpty() ? d.franjas().get(0).desde() : null,
                            d.franjas() != null && !d.franjas().isEmpty() ? d.franjas().get(0).hasta() : null,
                            null) // motivo no forma parte del contrato nuevo
            ).toList());
        }
        // fallback: tu endpoint anterior
        if (from == null || to == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.calendario(from, to));
    }

    // ===== NUEVOS =====

    // POST /api/calendario/habilitar  { fecha }
    @PostMapping("/habilitar")
    public ResponseEntity<Void> habilitar(@RequestBody @Valid FechaRequest body) {
        service.habilitarDia(body.fecha());
        return ResponseEntity.noContent().build();
    }

    // POST /api/calendario/deshabilitar  { fecha, motivo? }
    @PostMapping("/deshabilitar")
    public ResponseEntity<Void> deshabilitar(@RequestBody @Valid DeshabilitarRequest body) {
        service.deshabilitarDia(body.fecha(), body.motivo());
        return ResponseEntity.noContent().build();
    }

    // PUT /api/calendario/horarios  { fecha, franjas:[{desde,hasta}] }
    @PutMapping("/horarios")
    public ResponseEntity<Void> cambiarHorarios(@RequestBody @Valid HorariosRequest body) {
        service.cambiarHorarios(body.fecha(), body.franjas());
        return ResponseEntity.noContent().build();
    }
}
