// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/DevCuotaDebugController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.services.CuotaService;

@RestController
@RequestMapping("/api/dev/cuotas")
@CrossOrigin(origins = "*")
public class DevCuotaDebugController {

    private final CuotaService cuotaService;

    public DevCuotaDebugController(CuotaService cuotaService) {
        this.cuotaService = cuotaService;
    }

    // 👉 Probar generar cuotas de un mes
    // POST /api/dev/cuotas/generar?mes=2025-11-01
    @PostMapping("/generar")
    public ResponseEntity<String> generarCuotasMes(
            @RequestParam(required = false) String mes
    ) {
        LocalDate mesParam = (mes != null) ? LocalDate.parse(mes) : LocalDate.now();
        int creadas = cuotaService.generarCuotasMes(mesParam);
        return ResponseEntity.ok("Cuotas generadas para " + mesParam + ": " + creadas);
    }

    // 👉 Probar aplicar recargos de mora
// POST /api/dev/cuotas/aplicar-recargos?hoy=2025-12-11
@PostMapping("/aplicar-recargos")
public ResponseEntity<String> aplicarRecargos(
        @RequestParam(required = false) String hoy
) {
    LocalDate fechaHoy = (hoy != null) ? LocalDate.parse(hoy) : LocalDate.now();

    int procesadas = cuotaService.aplicarRecargosMora(fechaHoy); // este método lo definís en el service

    return ResponseEntity.ok("Recargos aplicados. Cuotas procesadas: " + procesadas);
}

}
