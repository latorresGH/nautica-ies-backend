package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.models.CierreExcepcional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CierreExcepcionalRepository extends JpaRepository<CierreExcepcional, Long> {
    Optional<CierreExcepcional> findByFecha(LocalDate fecha);
    List<CierreExcepcional> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
