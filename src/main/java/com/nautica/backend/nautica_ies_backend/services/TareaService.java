// src/main/java/com/nautica/backend/nautica_ies_backend/services/TareaService.java
package com.nautica.backend.nautica_ies_backend.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;
import com.nautica.backend.nautica_ies_backend.repository.OperarioRepository;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.BarSemana;


@Service
public class TareaService {

    private final TareaRepository repo;
    private final OperarioRepository operarioRepo;

    public TareaService(TareaRepository repo, OperarioRepository operarioRepo) {
        this.repo = repo;
        this.operarioRepo = operarioRepo;
    }

    public Page<Tarea> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Tarea obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    public Tarea buscarPorNumero(Integer numero) {
        return repo.findByNumeroTarea(numero).orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    public List<Tarea> listarPorOperario(Long idOperario) {
        return repo.findByOperario_IdUsuario(idOperario);
    }

    public Tarea crear(Tarea t) {
        if (t.getOperario() == null || t.getOperario().getIdUsuario() == null) {
            throw new IllegalArgumentException("Debe indicar el operario (idUsuario)");
        }
        Operario op = operarioRepo.findById(t.getOperario().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
        t.setOperario(op);
        try {
            return repo.save(t);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Número de tarea duplicado");
        }
    }

    public Tarea actualizar(Long id, Tarea datos) {
        Tarea t = obtener(id);

        t.setNumeroTarea(datos.getNumeroTarea());
        t.setTipoTarea(datos.getTipoTarea());
        t.setFecha(datos.getFecha());
        t.setHora(datos.getHora());
        t.setEstado(datos.getEstado());

        if (datos.getOperario() != null && datos.getOperario().getIdUsuario() != null) {
            Operario op = operarioRepo.findById(datos.getOperario().getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Operario no encontrado"));
            t.setOperario(op);
        }

        try {
            return repo.save(t);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Número de tarea duplicado");
        }
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Tarea no encontrada");
        repo.deleteById(id);
    }

        public List<Tarea> listarPorFecha(LocalDate fecha) {
        return repo.findByFechaOrderByHoraAsc(fecha);
    }

    // 🔹 NUEVO: resumen semanal para el gráfico
    public BarSemana resumenSemana(int offset) {
        // Misma zona que el Dashboard
        LocalDate hoy = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));

        // offset=0 -> semana actual, offset=1 -> semana pasada (S-1)
        LocalDate base = hoy.minusWeeks(offset);
        LocalDate lunes   = base.with(DayOfWeek.MONDAY);
        LocalDate domingo = base.with(DayOfWeek.SUNDAY);

        // ⚠️ Usa el nombre real de tu enum
        // Si tu enum es EstadoTarea.realizada (minúscula), dejalo así:
        List<Tarea> realizadas = repo.findByFechaBetweenAndEstado(
                lunes,
                domingo,
                EstadoTarea.realizado
        );

        Map<LocalDate, Long> conteo = new HashMap<>();
        for (Tarea t : realizadas) {
            LocalDate f = t.getFecha();
            conteo.put(f, conteo.getOrDefault(f, 0L) + 1L);
        }

        List<String> labels = new ArrayList<>();
        List<Long> values   = new ArrayList<>();

        LocalDate cursor = lunes;
        while (!cursor.isAfter(domingo)) {
            DayOfWeek dow = cursor.getDayOfWeek();
            String label = switch (dow) {
                case MONDAY    -> "Lun";
                case TUESDAY   -> "Mar";
                case WEDNESDAY -> "Mié";
                case THURSDAY  -> "Jue";
                case FRIDAY    -> "Vie";
                case SATURDAY  -> "Sáb";
                case SUNDAY    -> "Dom";
            };

            labels.add(label);
            values.add(conteo.getOrDefault(cursor, 0L));

            cursor = cursor.plusDays(1);
        }

        return new BarSemana(labels, values);
    }

}
