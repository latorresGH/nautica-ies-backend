// EmbarcacionRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Collection;
import java.util.Optional;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> {
    Optional<Embarcacion> findByNumMatricula(String numMatricula);

    @Modifying
    void deleteByIdEmbarcacionIn(Collection<Long> ids);


}
