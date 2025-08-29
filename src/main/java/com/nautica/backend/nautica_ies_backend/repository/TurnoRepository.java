package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nautica.backend.nautica_ies_backend.models.Turno;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

  // Overlap por embarcación (misma fecha, franja que se pisa)
  @Query("""
    select (count(t) > 0)
    from Turno t
    where t.fecha = :fecha
      and t.embarcacion.idEmbarcacion = :idEmbarcacion
      and (t.horaInicio < :horaFin and t.horaFin > :horaInicio)
      and (:idTurnoExcluido is null or t.id <> :idTurnoExcluido)
  """)
  boolean existsOverlap(LocalDate fecha,
                        LocalTime horaInicio,
                        LocalTime horaFin,
                        Long idEmbarcacion,
                        Long idTurnoExcluido);

  // Cliente y Operario heredan id como idUsuario
  List<Turno> findByCliente_IdUsuarioOrderByFechaDescHoraInicioDesc(Long idCliente);

  List<Turno> findByOperario_IdUsuarioAndFechaBetweenOrderByFechaAscHoraInicioAsc(
      Long idOperario, LocalDate from, LocalDate to);

  List<Turno> findByFechaOrderByHoraInicioAsc(LocalDate fecha);
}
