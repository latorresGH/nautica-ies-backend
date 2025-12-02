package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.models.Usuario;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    long countByFecha(LocalDate fecha);
    
    List<Turno> findByFecha(LocalDate fecha);


    // ✅ Turnos de un día, ordenados por hora
    List<Turno> findByFechaOrderByHoraInicioAsc(LocalDate fecha);

    // ✅ todos los turnos del cliente, más recientes primero
    List<Turno> findByCliente_IdUsuarioOrderByFechaDescHoraInicioDesc(Long clienteId);

    // ✅ todos los turnos del cliente, más antiguos primero
    List<Turno> findByCliente_IdUsuarioOrderByFechaAscHoraInicioAsc(Long clienteId);

    // ✅ turnos de un operario en un rango
    List<Turno> findByOperario_IdUsuarioAndFechaBetweenOrderByFechaAscHoraInicioAsc(
            Long idOperario,
            LocalDate from,
            LocalDate to
    );

    // ✅ chequear solapamiento para una embarcación
    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM Turno t
        WHERE t.fecha = :fecha
          AND t.embarcacion.idEmbarcacion = :idEmbarcacion
          AND (:idTurnoExcluido IS NULL OR t.id <> :idTurnoExcluido)
          AND t.horaInicio < :fin AND t.horaFin > :inicio
        """)
    boolean existsOverlap(
            @Param("fecha") LocalDate fecha,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin,
            @Param("idEmbarcacion") Long idEmbarcacion,
            @Param("idTurnoExcluido") Long idTurnoExcluido
    );

    @Query("""
    SELECT COUNT(t)
    FROM Turno t
    WHERE t.fecha = :fecha
      AND t.horaInicio < :fin
      AND t.horaFin > :inicio
    """)
long countOverlapInFecha(
        @Param("fecha") LocalDate fecha,
        @Param("inicio") LocalTime inicio,
        @Param("fin") LocalTime fin);


    

    // ❌ ELIMINAMOS esto por ahora:
    // @Query buscarProximoTurno(...)
    // porque Turno ya no tiene estado ni fechaTurno,
    // y además el front ya calcula el próximo turno con useEffect.
}
