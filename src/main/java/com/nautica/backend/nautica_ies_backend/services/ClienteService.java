package com.nautica.backend.nautica_ies_backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad
 * {@link Cliente}.
 * 
 * Proporciona métodos para gestionar clientes: listar, crear, obtener por ID,
 * actualizar y eliminar.
 */
@Service
public class ClienteService {

    private final ClienteRepository repo;

    /**
     * Constructor que inyecta el repositorio de clientes.
     *
     * @param repo Repositorio de clientes.
     */
    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    /**
     * Lista los clientes de forma paginada y ordenada.
     *
     * @param page Número de página (por defecto 0).
     * @param size Tamaño de la página (por defecto 25).
     * @param sort Campo y dirección de orden (por defecto "idCliente,asc").
     * @return Página de clientes.
     */
    public Page<Cliente> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * Obtiene una lista de todos los clientes registrados en la base de datos.
     *
     * @return Lista de clientes.
     */
    public List<Cliente> listar() {
        return repo.findAll();
    }

    /**
     * Crea un nuevo cliente y lo guarda en la base de datos.
     *
     * @param cliente Objeto cliente con los datos a registrar.
     * @return Cliente creado y persistido.
     */
    public Cliente crear(Cliente cliente) {
        try {
            return repo.save(cliente);
        } catch (DataIntegrityViolationException e) {
            // num_cliente UNIQUE u otras restricciones
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Violación de restricción ¿numCliente duplicado?");
        }
    }

    /**
     * Obtiene un cliente a partir de su ID.
     * 
     * Si el cliente no existe, lanza una excepción.
     *
     * @param id ID del cliente a buscar.
     * @return Cliente correspondiente al ID.
     * @throws RuntimeException si no se encuentra el cliente.
     */
    public Cliente obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id    ID del cliente a actualizar.
     * @param datos Objeto cliente con los nuevos datos.
     * @return Cliente actualizado y guardado en la base de datos.
     */
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id); // 404 si no existe

        cliente.setNumCliente(datos.getNumCliente());
        cliente.setEstadoCliente(datos.getEstadoCliente());
        cliente.setTipoCliente(datos.getTipoCliente());
        cliente.setEmbarcacion(datos.getEmbarcacion());

        try {
            return repo.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Violación de restricción ¿numCliente duplicado?");
        }
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param id ID del cliente a eliminar.
     */
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        repo.deleteById(id);
    }

    /**
     * Busca un cliente por su número de cliente.
     * Si no se encuentra, lanza una excepción.
     */
    public Cliente buscarPorNumero(Integer numCliente) {
        return repo.findByNumCliente(numCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

        /**
     * Cuenta el número total de usuarios.
     * @return
     */
    public long contarTodos() {
        return repo.count();
    }

    /**
     * Cuenta el número de usuarios activos.
     * 
     * @return número de usuarios activos.
     */
    public long contarActivos() {
        return repo.countByActivoTrue();
    }
}
