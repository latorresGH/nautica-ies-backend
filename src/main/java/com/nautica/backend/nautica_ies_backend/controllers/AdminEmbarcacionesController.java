// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/AdminEmbarcacionesController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Resumen.EmbarcacionAdminDTO;
import com.nautica.backend.nautica_ies_backend.services.UsuarioEmbarcacionService;

@RestController
@RequestMapping("/api/admin/embarcaciones")
public class AdminEmbarcacionesController {

    private final UsuarioEmbarcacionService usuarioEmbarcacionService;

    public AdminEmbarcacionesController(UsuarioEmbarcacionService usuarioEmbarcacionService) {
        this.usuarioEmbarcacionService = usuarioEmbarcacionService;
    }

    @GetMapping
    public ResponseEntity<List<EmbarcacionAdminDTO>> listar() {
        return ResponseEntity.ok(usuarioEmbarcacionService.listarEmbarcacionesAdmin());
    }
}
