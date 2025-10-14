// src/main/java/com/nautica/backend/nautica_ies_backend/repository/OperarioRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nautica.backend.nautica_ies_backend.models.Operario;

public interface OperarioRepository extends JpaRepository<Operario, Long> {
    Optional<Operario> findByLegajo(String legajo);

    boolean existsByLegajo(String legajo);

    @Query("""
        SELECT o FROM Operario o
        WHERE (:q IS NULL OR :q = '' OR
              LOWER(o.nombre)  LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(o.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(o.correo)   LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(o.legajo)   LIKE LOWER(CONCAT('%', :q, '%')) OR
              LOWER(o.puesto)   LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    Page<Operario> search(@Param("q") String q, Pageable pageable);
}
