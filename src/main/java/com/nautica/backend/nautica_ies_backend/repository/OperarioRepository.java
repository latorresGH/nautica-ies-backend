// src/main/java/com/nautica/backend/nautica_ies_backend/repository/OperarioRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nautica.backend.nautica_ies_backend.models.Operario;

public interface OperarioRepository extends JpaRepository<Operario, Long> {
    Optional<Operario> findByLegajo(String legajo);

    boolean existsByLegajo(String legajo);
}
