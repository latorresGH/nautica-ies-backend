package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    boolean existsByClienteAndEmbarcacionAndNumeroMes(Cliente c, Embarcacion e, LocalDate numeroMes);

    Optional<Cuota> findTopByClienteAndEmbarcacionOrderByNumeroPagoDesc(Cliente c, Embarcacion e);

    List<Cuota> findByCliente_IdUsuarioAndNumeroMesBetween(Long idCliente, LocalDate desde, LocalDate hasta);

    List<Cuota> findByEmbarcacion_IdEmbarcacionAndNumeroMesBetween(Long idEmbarcacion, LocalDate desde,
            LocalDate hasta);
}
