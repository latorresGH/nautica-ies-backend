// AnuncioRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Anuncio;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByFechaExpiracionIsNullOrFechaExpiracionGreaterThanEqualOrderByFechaPublicacionDescHoraPublicacionDesc(
            LocalDate fecha);
}
