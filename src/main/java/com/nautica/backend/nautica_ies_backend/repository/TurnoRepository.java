package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nautica.backend.nautica_ies_backend.models.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
  long countByFechaTurno(LocalDate fecha);

  @Query("""
        SELECT (COUNT(t) > 0)
        FROM Turno t
        WHERE t.fechaTurno = :fecha
          AND t.embarcacion.idEmbarcacion = :idEmbarcacion
          AND (t.horaInicio < :horaFin AND t.horaFin > :horaInicio)
          AND (:idTurnoExcluido IS NULL OR t.id <> :idTurnoExcluido)
      """)
  boolean existsOverlap(@Param("fecha") LocalDate fecha,
                        @Param("horaInicio") LocalTime horaInicio,
                        @Param("horaFin") LocalTime horaFin,
                        @Param("idEmbarcacion") Long idEmbarcacion,
                        @Param("idTurnoExcluido") Long idTurnoExcluido);

  // Cliente y Operario heredan id como idUsuario
  List<Turno> findByCliente_IdUsuarioOrderByFechaDescHoraInicioDesc(Long idCliente);

  List<Turno> findByOperario_IdUsuarioAndFechaBetweenOrderByFechaAscHoraInicioAsc(
      Long idOperario, LocalDate from, LocalDate to);

  List<Turno> findByFechaOrderByHoraInicioAsc(LocalDate fecha);
}
