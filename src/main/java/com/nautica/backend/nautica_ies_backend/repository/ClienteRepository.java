package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente.ClienteSummary;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("""
        SELECT new com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente.ClienteSummary(
            c.idUsuario,         
            c.nombre,
            c.apellido,
            c.correo,
            c.telefono,
            c.activo
        )
        FROM Cliente c
        WHERE (:buscar IS NULL OR :buscar = '' OR
               LOWER(c.nombre)   LIKE LOWER(CONCAT('%', :buscar, '%')) OR
               LOWER(c.apellido) LIKE LOWER(CONCAT('%', :buscar, '%')) OR
               LOWER(c.correo)   LIKE LOWER(CONCAT('%', :buscar, '%')) OR
               c.telefono        LIKE CONCAT('%', :buscar, '%') OR
               CAST(c.dni AS string) LIKE CONCAT('%', :buscar, '%')
        )
        ORDER BY c.apellido ASC, c.nombre ASC
        """)
    Page<ClienteSummary> buscar(@Param("buscar") String buscar, Pageable pageable);

    Optional<Cliente> findByNumCliente(Integer numCliente);

    boolean existsByNumCliente(Integer numCliente);

    /**
     * Contar clientes a través del estado activo.
     */
    long countByActivoTrue();

    @Modifying
    @Query(value = "DELETE FROM clientes WHERE id_cliente = :id", nativeQuery = true)
    void deleteRowOnlyFromClientes(@Param("id") Long id);

    // siguiente número de cliente (max + 1)
    @Query("select coalesce(max(c.numCliente), 0) + 1 from Cliente c")
    Integer siguienteNumeroCliente();
}
