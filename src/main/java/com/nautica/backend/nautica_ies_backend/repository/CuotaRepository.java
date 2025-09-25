package com.nautica.backend.nautica_ies_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
long countByEstado(EstadoCuota estado);

    boolean existsByClienteAndEmbarcacionAndNumeroMes(Cliente c, Embarcacion e, LocalDate numeroMes);

    Optional<Cuota> findTopByClienteAndEmbarcacionOrderByNumeroPagoDesc(Cliente c, Embarcacion e);

    List<Cuota> findByCliente_IdUsuarioAndNumeroMesBetween(Long idCliente, LocalDate desde, LocalDate hasta);

    List<Cuota> findByEmbarcacion_IdEmbarcacionAndNumeroMesBetween(Long idEmbarcacion, LocalDate desde, LocalDate hasta);

    // ===== Ingresos del mes (si usás numeroMes como "periodo" del devengo)
    @Query("""
        SELECT COALESCE(SUM(c.monto), 0)
        FROM Cuota c
        WHERE c.estado = :estado
          AND c.numeroMes BETWEEN :desde AND :hasta
    """)
    BigDecimal sumMontoPagadoPorNumeroMes(@Param("estado") EstadoCuota estado,
                                          @Param("desde") LocalDate desde,
                                          @Param("hasta") LocalDate hasta);

    // ===== KPIs por cliente distinto en el mes (usando herencia: Cliente.id = idUsuario)
    @Query("""
        SELECT COUNT(DISTINCT c.cliente.idUsuario)
        FROM Cuota c
        WHERE c.numeroMes BETWEEN :desde AND :hasta
          AND c.estado = :estado
    """)
    long countDistinctClientesPorEstadoEnMes(@Param("estado") EstadoCuota estado,
                                             @Param("desde") LocalDate desde,
                                             @Param("hasta") LocalDate hasta);

    @Query("""
        SELECT COUNT(DISTINCT c.cliente.idUsuario)
        FROM Cuota c
        WHERE c.numeroMes BETWEEN :desde AND :hasta
          AND c.estado IN :estados
    """)
    long countDistinctClientesPorEstadosEnMes(@Param("estados") List<EstadoCuota> estados,
                                              @Param("desde") LocalDate desde,
                                              @Param("hasta") LocalDate hasta);
}
