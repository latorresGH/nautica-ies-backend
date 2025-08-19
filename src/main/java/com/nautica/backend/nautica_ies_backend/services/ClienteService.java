package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente crear(Cliente cliente) {
        return repo.save(cliente);
    }

    public Cliente obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id);
        cliente.setNumCliente(datos.getNumCliente());
        cliente.setEstadoCliente(datos.getEstadoCliente());
        cliente.setTipoCliente(datos.getTipoCliente());
        cliente.setEmbarcacion(datos.getEmbarcacion());
        return repo.save(cliente);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
