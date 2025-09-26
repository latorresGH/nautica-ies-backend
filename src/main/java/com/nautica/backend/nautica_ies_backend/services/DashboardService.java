// src/main/java/com/nautica/backend/nautica_ies_backend/services/DashboardService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.controllers.dto.DashboardDTO;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;
import com.nautica.backend.nautica_ies_backend.repository.CuotaRepository;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;
import com.nautica.backend.nautica_ies_backend.repository.TurnoRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

@Service
public class DashboardService {

  private final UsuarioRepository usuariosRepo;
  private final TareaRepository tareasRepo;
  private final TurnoRepository turnosRepo;
  private final CuotaRepository cuotasRepo;

  public DashboardService(
      UsuarioRepository usuariosRepo,
      TareaRepository tareasRepo,
      TurnoRepository turnosRepo,
      CuotaRepository cuotasRepo
  ) {
    this.usuariosRepo = usuariosRepo;
    this.tareasRepo = tareasRepo;
    this.turnosRepo = turnosRepo;
    this.cuotasRepo = cuotasRepo;
  }

  @Transactional(readOnly = true)
  public DashboardDTO kpis(LocalDate fecha) {
    long usuariosActivos   = usuariosRepo.countByActivoTrue();
    long tareasHoy         = tareasRepo.countByFecha(fecha);
    long tareaspendientes  = tareasRepo.countByFechaAndEstado(fecha, EstadoTarea.pendiente);
    long turnosHoy         = turnosRepo.countByFecha(fecha);
    long cuotasvencidas    = cuotasRepo.countByEstadoCuota(EstadoCuota.vencida);

    // Rango del mes (1er día .. último día)
    YearMonth ym = YearMonth.from(fecha);
    LocalDate desde = ym.atDay(1);
    LocalDate hasta = ym.atEndOfMonth();

    // Ingresos (por numeroMes, si no usás fechaPago)
 BigDecimal ingresosMes =
      cuotasRepo.sumMontoPagadoPorNumeroMes(EstadoCuota.pagada, desde, hasta);

    // Clientes que PAGARON (distintos) en el mes
    long clientesPagaronMes = cuotasRepo
        .countDistinctClientesPorEstadoEnMes(EstadoCuota.pagada, desde, hasta);

    // Clientes que DEBEN (distintos) en el mes: pendiente o vencida
    long clientesDebenMes = cuotasRepo
        .countDistinctClientesPorEstadosEnMes(
            List.of(EstadoCuota.pendiente, EstadoCuota.vencida),
            desde, hasta
        );

    return new DashboardDTO(
        fecha,
        usuariosActivos,
        tareasHoy,
        tareaspendientes,
        turnosHoy,
        cuotasvencidas,
        ingresosMes,
        clientesDebenMes,
        clientesPagaronMes
    );
  }
}
