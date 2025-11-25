package com.nautica.backend.nautica_ies_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Cuota;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {

  long countByEstadoCuota(EstadoCuota estado);

  boolean existsByClienteAndEmbarcacionAndNumeroMes(Cliente c, Embarcacion e, LocalDate numeroMes);

  Optional<Cuota> findTopByCliente_IdUsuarioOrderByNumeroMesDesc(Long clienteId);

  Optional<Cuota> findTopByClienteAndEmbarcacionOrderByNumeroPagoDesc(Cliente c, Embarcacion e);

  List<Cuota> findByCliente_IdUsuarioAndNumeroMesBetween(Long idCliente, LocalDate desde, LocalDate hasta);

  List<Cuota> findByEmbarcacion_IdEmbarcacionAndNumeroMesBetween(Long idEmbarcacion, LocalDate desde, LocalDate hasta);

  List<Cuota> findByCliente_IdUsuarioOrderByNumeroMesDesc(Long clienteId);
  long countByCliente_IdUsuarioAndEstadoCuotaIn(Long idUsuario, java.util.Collection<EstadoCuota> estados);

  @org.springframework.data.jpa.repository.Modifying
  void deleteByCliente_IdUsuario(Long idUsuario);

  Optional<Cuota> findTopByCliente_IdUsuarioAndEmbarcacion_IdEmbarcacionOrderByNumeroMesDesc(
      Long clienteId, Long embarcacionId);

  // SUM ingresos por periodo usando numeroMes
  @Query("""
        SELECT COALESCE(SUM(c.monto), 0)
        FROM Cuota c
        WHERE c.estadoCuota = :estado
          AND c.numeroMes BETWEEN :desde AND :hasta
      """)
  BigDecimal sumMontoPagadoPorNumeroMes(@Param("estado") EstadoCuota estado,
      @Param("desde") LocalDate desde,
      @Param("hasta") LocalDate hasta);

  // Distintos clientes que pagaron/deben en el mes (no dependemos del nombre del
  // id)
  @Query("""
        SELECT COUNT(DISTINCT c.cliente)
        FROM Cuota c
        WHERE c.numeroMes BETWEEN :desde AND :hasta
          AND c.estadoCuota = :estado
      """)
  long countDistinctClientesPorEstadoEnMes(@Param("estado") EstadoCuota estado,
      @Param("desde") LocalDate desde,
      @Param("hasta") LocalDate hasta);

  @Query("""
        SELECT COUNT(DISTINCT c.cliente)
        FROM Cuota c
        WHERE c.numeroMes BETWEEN :desde AND :hasta
          AND c.estadoCuota IN :estados
      """)
  long countDistinctClientesPorEstadosEnMes(@Param("estados") List<EstadoCuota> estados,
      @Param("desde") LocalDate desde,
      @Param("hasta") LocalDate hasta);

  @Query("""
        SELECT c FROM Cuota c
        WHERE c.estadoCuota = com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota.pagada
          AND (:clienteId IS NULL OR c.cliente.idUsuario = :clienteId)
          AND (:desde IS NULL OR c.fechaPago >= :desde)
          AND (:hasta IS NULL OR c.fechaPago <= :hasta)
          AND (:medio IS NULL OR c.formaPago = :medio)
        ORDER BY c.fechaPago DESC, c.idCuota DESC
      """)
  Page<Cuota> buscarPagos(@Param("clienteId") Long clienteId,
      @Param("desde") java.time.LocalDate desde,
      @Param("hasta") java.time.LocalDate hasta,
      @Param("medio") com.nautica.backend.nautica_ies_backend.models.enums.FormaPago medio,
      Pageable pageable);

        boolean existsByNumeroMes(LocalDate numeroMes);
            
List<Cuota> findByCliente_IdUsuarioAndNumeroMes(Long clienteId, LocalDate numeroMes);

}
