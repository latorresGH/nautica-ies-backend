package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.services.ClienteService;

import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de clientes.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /**
     * Lista paginada/ordenada de clientes.
     * Ej: GET /api/clientes?page=0&size=25&sort=idUsuario,asc
     *
     * IMPORTANTE: como Cliente hereda de Usuario, el id es idUsuario (del padre).
     */
    @GetMapping
    public ResponseEntity<Page<Cliente>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idUsuario,asc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    /**
     * Obtiene un cliente por ID (idUsuario del padre).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id)); // 200 o 404 (vía handler global)
    }

    /**
     * Busca un cliente por su número de cliente.
     * Ej: GET /api/clientes/by-num?valor=123
     */
    @GetMapping("/by-num")
    public ResponseEntity<Cliente> porNumero(@RequestParam("valor") Integer numCliente) {
        return ResponseEntity.ok(service.buscarPorNumero(numCliente));
    }

    // /**
    //  * Crea un nuevo cliente.
    //  * SUGERENCIA: el service debe setear rol=CLIENTE y codificar contraseña
    //  * (heredada).
    //  */
    // @PostMapping
    // public ResponseEntity<Cliente> crear(@RequestBody @Valid Cliente cliente,
    //         UriComponentsBuilder uriBuilder) {
    //     Cliente creado = service.crear(cliente);
    //     var location = uriBuilder.path("/api/clientes/{id}")
    //             .buildAndExpand(creado.getIdUsuario()) // <- usar idUsuario heredado
    //             .toUri();
    //     return ResponseEntity.created(location).body(creado);
    // }

    /**
     * Actualiza un cliente existente.
     * Recomiendo que el service valide que el rol siga siendo CLIENTE.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,
            @RequestBody @Valid Cliente cliente) {
        return ResponseEntity.ok(service.actualizar(id, cliente));
    }

    /**
     * Elimina un cliente por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
