// EmbarcacionRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;

public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> {
    Optional<Embarcacion> findByNumMatricula(String numMatricula);
}
