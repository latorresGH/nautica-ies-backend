package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.services.ClienteService;

/**
 * Controlador REST para la gestión de clientes.
 * <p>
 * Proporciona endpoints para realizar operaciones CRUD sobre la entidad {@link Cliente}.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    /**
     * Constructor que inyecta el servicio de clientes.
     *
     * @param service Servicio encargado de la lógica de negocio para clientes.
     */
    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /**
     * Obtiene la lista completa de clientes registrados.
     *
     * @return Lista de clientes.
     */
    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id ID del cliente.
     * @return Cliente correspondiente al ID.
     */
    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param cliente Objeto cliente con los datos a registrar.
     * @return Cliente creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@RequestBody Cliente cliente) {
        return service.crear(cliente);
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param id      ID del cliente a actualizar.
     * @param cliente Datos nuevos del cliente.
     * @return Cliente actualizado.
     */
    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return service.actualizar(id, cliente);
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param id ID del cliente a eliminar.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
