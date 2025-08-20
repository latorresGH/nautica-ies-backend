package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;


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
        public ResponseEntity<Page<Cliente>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idCliente,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id ID del cliente.
     * @return Cliente correspondiente al ID.
     */
    @GetMapping("/{id}")
        public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id)); // 200 o 404 (via handler global)
    }

    /**
     * Busca un cliente por su número de cliente.
     *
     * @param numCliente Número de cliente a buscar.
     * @return Cliente correspondiente al número.
     */
    @GetMapping("/by-num")
    public ResponseEntity<Cliente> porNumero(@RequestParam("valor") Integer numCliente) {
        return ResponseEntity.ok(service.buscarPorNumero(numCliente));
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param cliente Objeto cliente con los datos a registrar.
     * @return Cliente creado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
        public ResponseEntity<Cliente> crear(@RequestBody @Valid Cliente cliente,
                                         UriComponentsBuilder uriBuilder) {
        Cliente creado = service.crear(cliente);
        var location = uriBuilder.path("/api/clientes/{id}")
                                 .buildAndExpand(creado.getIdCliente())
                                 .toUri();
        return ResponseEntity.created(location).body(creado); // 201 + Location header
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param id      ID del cliente a actualizar.
     * @param cliente Datos nuevos del cliente.
     * @return Cliente actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,
                                              @RequestBody @Valid Cliente cliente) {
        return ResponseEntity.ok(service.actualizar(id, cliente)); // 200
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param id ID del cliente a eliminar.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
