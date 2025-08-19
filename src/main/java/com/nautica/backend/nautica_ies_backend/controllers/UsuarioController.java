// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/UsuarioController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET paginado: /api/usuarios?page=0&size=10&sort=apellido,asc
    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idUsuario,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id)); // 200 o 404 (via handler)
    }

    // GET por correo: /api/usuarios/by-correo?correo=juan@nautica.com
    @GetMapping("/by-correo")
    public ResponseEntity<Usuario> porCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(service.buscarPorCorreo(correo));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody @Valid Usuario usuario, UriComponentsBuilder uriBuilder) {
        Usuario creado = service.crear(usuario);
        var location = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(creado.getIdUsuario()).toUri();
        return ResponseEntity.created(location).body(creado); // 201 + Location header
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody @Valid Usuario usuario) {
        return ResponseEntity.ok(service.actualizar(id, usuario)); // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
