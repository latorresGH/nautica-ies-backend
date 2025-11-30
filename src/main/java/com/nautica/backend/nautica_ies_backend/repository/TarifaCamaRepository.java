// src/main/java/com/nautica/backend/nautica_ies_backend/repository/TarifaCamaRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.TarifaCama;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoCama;

public interface TarifaCamaRepository extends JpaRepository<TarifaCama, Long> {

    // 👉 tu método:
    Optional<TarifaCama> findByTipoCamaAndNumeroMes(TipoCama tipoCama, LocalDate numeroMes);

    // todas las tarifas disponibles para un mes
    List<TarifaCama> findByNumeroMes(LocalDate numeroMes);

    Optional<TarifaCama> findTopByTipoCamaAndNumeroMesLessThanEqualOrderByNumeroMesDesc(
            TipoCama tipoCama,
            LocalDate numeroMes);

}
