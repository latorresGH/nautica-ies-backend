package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.models.HorarioOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HorarioOperacionRepository extends JpaRepository<HorarioOperacion, Long> {
    Optional<HorarioOperacion> findByDiaSemana(Integer diaSemana);
}
