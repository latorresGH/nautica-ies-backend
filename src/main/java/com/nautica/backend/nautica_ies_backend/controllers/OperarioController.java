// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/OperarioController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioCreateRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioEstadoPatch;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioResponse;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Operarios.OperarioUpdateRequest;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.services.OperarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/operarios")
public class OperarioController {

    private final OperarioService service;

    public OperarioController(OperarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<OperarioResponse>> listar(
            @RequestParam(defaultValue = "") String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idUsuario,asc") String sort) {

        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);

        var raw = (buscar == null || buscar.isBlank())
                ? service.listar(page, size, sortObj)
                : service.listarConBusqueda(buscar, page, size, sortObj);

        return ResponseEntity.ok(service.toResponsePage(raw));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.toResponse(service.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<OperarioResponse> crear(@RequestBody @Valid OperarioCreateRequest req,
                                                  UriComponentsBuilder uriBuilder) {
        Operario creado = service.crearDesdeDto(req);
        var location = uriBuilder.path("/api/operarios/{id}")
                .buildAndExpand(creado.getIdUsuario())
                .toUri();
        return ResponseEntity.created(location).body(service.toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperarioResponse> actualizar(@PathVariable Long id,
                                                       @RequestBody @Valid OperarioUpdateRequest req) {
        Operario actualizado = service.actualizarDesdeDto(id, req);
        return ResponseEntity.ok(service.toResponse(actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OperarioResponse> cambiarEstado(@PathVariable Long id,
                                                          @RequestBody @Valid OperarioEstadoPatch body) {
        Operario actualizado = service.cambiarEstado(id, body.activo, body.motivo);
        return ResponseEntity.ok(service.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
