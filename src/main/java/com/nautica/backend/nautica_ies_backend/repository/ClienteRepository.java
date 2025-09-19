package com.nautica.backend.nautica_ies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Cliente}.
 * <p>
 * Proporciona operaciones CRUD básicas y puede ser extendido con consultas
 * personalizadas.
 * <p>
 * Hereda de {@link JpaRepository}, lo que incluye métodos como:
 * <ul>
 * <li>{@code findAll()}</li>
 * <li>{@code findById(Long)}</li>
 * <li>{@code save(Cliente)}</li>
 * <li>{@code deleteById(Long)}</li>
 * <li>...y muchos más.</li>
 * </ul>
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNumCliente(Integer numCliente);

    boolean existsByNumCliente(Integer numCliente);

    /**
     * 
     * Contar clientes a traves del estado activo
     * 
     * @return numero de clientes activos
     *  */ 
    long countByActivoTrue();
}
