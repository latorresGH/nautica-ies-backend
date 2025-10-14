package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente.*;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.services.ClienteService;

import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de clientes.
 * 
 * Se mantienen los endpoints existentes (devolviendo entidad Cliente)
 * y se agregan endpoints ADMIN con s bajo /api/clientes/admin
 * para listar/buscar, obtener detalle, editar datos y baja lógica.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /* ===========================================================
     * ===========   ENDPOINTS EXISTENTES (ENTIDAD)   ============
     * =========================================================== */

    /**
     * Lista paginada/ordenada de clientes (entidad).
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
     * Obtiene un cliente por ID (entidad).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id)); // 200 o 404 (vía handler global)
    }

    /**
     * Busca un cliente por su número de cliente (entidad).
     * Ej: GET /api/clientes/by-num?valor=123
     */
    @GetMapping("/by-num")
    public ResponseEntity<Cliente> porNumero(@RequestParam("valor") Integer numCliente) {
        return ResponseEntity.ok(service.buscarPorNumero(numCliente));
    }

    /**
     * Actualiza un cliente existente (entidad -> entidad).
     * Recomiendo que el service valide que el rol siga siendo CLIENTE.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,
            @RequestBody @Valid Cliente cliente) {
        return ResponseEntity.ok(service.actualizar(id, cliente));
    }

    /**
     * Elimina un cliente por su ID (entidad).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarTodos() {
        return ResponseEntity.ok(Map.of("total", service.contarTodos()));
    }

    @GetMapping("/count/activos")
    public ResponseEntity<Map<String, Long>> contarActivos() {
        return ResponseEntity.ok(Map.of("total", service.contarActivos()));
    }


    /* ===========================================================
     * ===========       ENDPOINTS ADMIN (s)       ============
     * =========================================================== */

    /**
     * Listado ADMIN con búsqueda y paginado ( resumen).
     * Ej: GET /api/clientes/admin?buscar=juan&page=0&size=20
     */
    @GetMapping("/admin")
    public ResponseEntity<Page<ClienteSummary>> listarAdmin(
            @RequestParam(required = false, name = "buscar", defaultValue = "") String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.listarAdmin(buscar, pageable));
    }

    /**
     * Detalle ADMIN por ID ( detalle).
     * Ej: GET /api/clientes/admin/42
     */
    @GetMapping("/admin/{id}")
    public ResponseEntity<ClienteDetail> obtenerAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerAdmin(id));
    }

    /**
     * Editar datos personales/contacto (ADMIN) vía .
     * No cambia estado/tipo/numCliente aquí.
     * Ej: PUT /api/clientes/admin/42
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<ClienteDetail> editarAdmin(
            @PathVariable Long id,
            @RequestBody @Valid ClienteUpdateRequest body
    ) {
        return ResponseEntity.ok(service.editarAdmin(id, body));
    }

    /**
     * Baja lógica (ADMIN): estado_cliente=INACTIVO y activo=false. Sin body.
     * Ej: PATCH /api/clientes/admin/42/baja
     */
    @PatchMapping("/admin/{id}/baja")
    public ResponseEntity<ClienteDetail> bajaAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(service.bajaAdmin(id));
    }
}
