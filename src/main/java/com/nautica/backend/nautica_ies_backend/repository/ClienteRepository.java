package com.nautica.backend.nautica_ies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

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

  @Query("""
        SELECT c FROM Cliente c
        WHERE (:q IS NULL OR :q = '' OR
              lower(c.nombre)   LIKE lower(concat('%', :q, '%')) OR
              lower(c.apellido) LIKE lower(concat('%', :q, '%')) OR
              lower(c.correo)   LIKE lower(concat('%', :q, '%')) OR
              c.telefono        LIKE concat('%', :q, '%') OR
              c.dni             LIKE concat('%', :q, '%'))
        ORDER BY c.idUsuario DESC
      """)
  Page<Cliente> buscar(@Param("q") String buscar, Pageable pageable);

  Optional<Cliente> findByNumCliente(Integer numCliente);

  boolean existsByNumCliente(Integer numCliente);

  /**
   * 
   * Contar clientes a traves del estado activo
   * 
   * @return numero de clientes activos
   */
  long countByActivoTrue();

  @org.springframework.data.jpa.repository.Modifying
  @org.springframework.data.jpa.repository.Query(value = "DELETE FROM clientes WHERE id_cliente = :id", nativeQuery = true)
  void deleteRowOnlyFromClientes(@org.springframework.data.repository.query.Param("id") Long id);

}
