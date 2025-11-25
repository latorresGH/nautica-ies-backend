// src/main/java/com/nautica/backend/nautica_ies_backend/repository/TareaRepository.java
package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    long countByFecha(LocalDate fecha);
    long countByFechaAndEstado(LocalDate fecha, EstadoTarea estado);


    Optional<Tarea> findByNumeroTarea(Integer numeroTarea);
    boolean existsByNumeroTarea(Integer numeroTarea);

    List<Tarea> findByOperario_IdUsuario(Long idOperario);
    List<Tarea> findByFecha(LocalDate fecha);
        // ya lo usás en DashboardService

    // también ya lo usás ahí:
    // long countByFechaAndEstado(LocalDate fecha, EstadoTarea estado);

    // 🔹 NUEVO para "tareas del día"
    List<Tarea> findByFechaOrderByHoraAsc(LocalDate fecha);
    // 🔹 NUEVO: tareas en un rango de fechas con un estado
    List<Tarea> findByFechaBetweenAndEstado(LocalDate desde, LocalDate hasta, EstadoTarea estado);
    Optional<Tarea> findByTurno_Id(Long id);
    Optional<Tarea> findTopByOrderByNumeroTareaDesc();
}
