package com.nautica.backend.nautica_ies_backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad {@link Cliente}.
 * 
 * Proporciona métodos para gestionar clientes: listar, crear, obtener por ID, actualizar y eliminar.
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
        return repo.save(cliente);
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
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id ID del cliente a actualizar.
     * @param datos Objeto cliente con los nuevos datos.
     * @return Cliente actualizado y guardado en la base de datos.
     */
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id);
        cliente.setNumCliente(datos.getNumCliente());
        cliente.setEstadoCliente(datos.getEstadoCliente());
        cliente.setTipoCliente(datos.getTipoCliente());
        cliente.setEmbarcacion(datos.getEmbarcacion());
        return repo.save(cliente);
    }
    /**
     * Elimina un cliente por su ID.
     *
     * @param id ID del cliente a eliminar.
     */
    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
