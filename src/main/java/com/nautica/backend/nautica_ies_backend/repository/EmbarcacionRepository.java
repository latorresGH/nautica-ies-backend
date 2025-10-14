// EmbarcacionRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> {
    Optional<Embarcacion> findByNumMatricula(String numMatricula);

@org.springframework.data.jpa.repository.Modifying
void deleteByIdEmbarcacionIn(java.util.Collection<Long> ids);


}
