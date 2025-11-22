package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.repository.TurnoRepository;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos.TurnoCliente;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.Turnos.TurnoSolicitudRequest;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.EmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.TurnoRepository;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;
import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TurnoService {

  private final TurnoRepository turnoRepo;
  private final TareaRepository tareaRepo;
  private final EmbarcacionRepository embarRepo;
  private final ClienteRepository clienteRepo;

  public TurnoService(TurnoRepository turnoRepo, TareaRepository tareaRepo, EmbarcacionRepository embarRepo,
      ClienteRepository clienteRepo) {
    this.turnoRepo = turnoRepo;
    this.tareaRepo = tareaRepo;
    this.embarRepo = embarRepo;
    this.clienteRepo = clienteRepo;
  }

  @Transactional
  public Turno crear(Turno turno) {
    validarHoras(turno.getHoraInicio(), turno.getHoraFin());
    verificarSolapamiento(null, turno.getFecha(), turno.getHoraInicio(), turno.getHoraFin(),
        turno.getEmbarcacion().getIdEmbarcacion());
    return turnoRepo.save(turno);
  }

  @Transactional(readOnly = true)
  public Turno obtener(Long id) {
    return turnoRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
  }

  @Transactional(readOnly = true)
  public List<Turno> listarPorCliente(Long idCliente) {
    return turnoRepo.findByCliente_IdUsuarioOrderByFechaDescHoraInicioDesc(idCliente);
  }

  @Transactional(readOnly = true)
  public List<Turno> listarPorOperarioEnRango(Long idOperario, LocalDate from, LocalDate to) {
    return turnoRepo.findByOperario_IdUsuarioAndFechaBetweenOrderByFechaAscHoraInicioAsc(idOperario, from, to);
  }

  @Transactional
  public Turno actualizar(Long id, Turno nuevo) {
    Turno existente = obtener(id);

    validarHoras(nuevo.getHoraInicio(), nuevo.getHoraFin());
    verificarSolapamiento(id, nuevo.getFecha(), nuevo.getHoraInicio(), nuevo.getHoraFin(),
        nuevo.getEmbarcacion().getIdEmbarcacion());

    existente.setFecha(nuevo.getFecha());
    existente.setHoraInicio(nuevo.getHoraInicio());
    existente.setHoraFin(nuevo.getHoraFin());
    existente.setCliente(nuevo.getCliente());
    existente.setEmbarcacion(nuevo.getEmbarcacion());
    existente.setOperario(nuevo.getOperario());

    return turnoRepo.save(existente);
  }

  @Transactional
  public void eliminar(Long id) {
    if (!turnoRepo.existsById(id))
      throw new EntityNotFoundException("Turno no encontrado");
    turnoRepo.deleteById(id);
  }

  private void validarHoras(LocalTime inicio, LocalTime fin) {
    if (!fin.isAfter(inicio))
      throw new IllegalArgumentException("hora_fin debe ser posterior a hora_inicio");
  }

  private void verificarSolapamiento(Long idTurnoExcluido,
      LocalDate fecha,
      LocalTime inicio,
      LocalTime fin,
      Long idEmbarcacion) {
    boolean overlap = turnoRepo.existsOverlap(fecha, inicio, fin, idEmbarcacion, idTurnoExcluido);
    if (overlap)
      throw new IllegalStateException("Ya existe un turno superpuesto para esa embarcación");
  }

  /*
   * =====================================================
   * ============ MÉTODOS PARA CLIENTE ===============
   * =====================================================
   */

  // 🔹 1) Listar turnos de un cliente como DTO para el front
  @Transactional(readOnly = true)
  public List<TurnoCliente> listarTurnosClienteDTO(Long idCliente) {
    return turnoRepo
        .findByCliente_IdUsuarioOrderByFechaDescHoraInicioDesc(idCliente)
        .stream()
        .map(this::toTurnoCliente)
        .toList();
  }

  // 🔹 2) Solicitar turno desde el DTO del cliente
  @Transactional
  public TurnoCliente solicitarTurno(TurnoSolicitudRequest req) {
    // Ojo: ajustá los getters del record a cómo lo definiste
    Cliente cliente = clienteRepo.findById(req.clienteId())
        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

    Embarcacion emb = embarRepo.findById(req.idEmbarcacion())
        .orElseThrow(() -> new EntityNotFoundException("Embarcación no encontrada"));

    Turno t = new Turno();
    t.setCliente(cliente);
    t.setEmbarcacion(emb);
    t.setFecha(req.fecha());
    t.setHoraInicio(req.horaInicio());

    // ⬇️ ya no usamos req.horaFin(): definimos un bloque fijo de 30 minutos
  // ⬅️ AHORA
LocalTime horaFin = req.horaInicio().plusMinutes(15);



    t.setHoraFin(horaFin);

    // ⬇️ NO tenemos estado/tipo en Turno, eso vive en Tarea
    // t.setEstado("pendiente"); // ❌ sacar
    // t.setTipo(req.tipo()); // ❌ sacar

    validarHoras(t.getHoraInicio(), t.getHoraFin());

// ya tenés el check por embarcación:
verificarSolapamiento(
    null,
    t.getFecha(),
    t.getHoraInicio(),
    t.getHoraFin(),
    t.getEmbarcacion().getIdEmbarcacion()
);

// ⬅️ nuevo: check global de capacidad (máx. 2)
verificarCapacidadGlobal(
    t.getFecha(),
    t.getHoraInicio(),
    t.getHoraFin()
);

Turno guardado = turnoRepo.save(t);

    // toTurnoCliente(...) se encarga de mirar la Tarea asociada (si existe)
    // y devolver estado/tipo al front. Si aún no hay Tarea, podés devolver
    // "pendiente" por defecto ahí.
    return toTurnoCliente(guardado);
  }

  // 🔹 3) Cancelar turno (para el botón "Cancelar" del front)
  @Transactional
  public TurnoCliente cancelarTurno(Long idTurno) {
    Turno turno = obtener(idTurno); // ya tenías este método

    Tarea tarea = tareaRepo.findByTurno_Id(idTurno)
        .orElseThrow(() -> new IllegalStateException("No hay tarea asociada a este turno"));

    tarea.setEstado(EstadoTarea.cancelado);
    tareaRepo.save(tarea);

    return toTurnoCliente(turno);
  }

  // 🔹 4) Mapper entidad -> DTO
  private TurnoCliente toTurnoCliente(Turno turno) {
    // buscamos la tarea asociada, si existe
    var optTarea = tareaRepo.findByTurno_Id(turno.getIdTurno());

    String estado = optTarea
        .map(t -> t.getEstado() != null ? t.getEstado().name() : null)
        .orElse("pendiente"); // si no hay tarea aún, lo tomamos como pendiente

    String tipo = optTarea
        .map(t -> t.getTipoTarea() != null ? t.getTipoTarea().name() : null)
        .orElse(null);

    Long idEmb = turno.getEmbarcacion() != null
        ? turno.getEmbarcacion().getIdEmbarcacion()
        : null;

    return new TurnoCliente(
        turno.getIdTurno(),
        turno.getFecha() != null ? turno.getFecha().toString() : null,
        turno.getHoraInicio() != null ? turno.getHoraInicio().toString() : null,
        turno.getHoraFin() != null ? turno.getHoraFin().toString() : null,
        estado,
        tipo,
        idEmb);
  }

@Transactional(readOnly = true)
public List<TurnoCliente> listarPorFechaDTO(LocalDate fecha) {
    return turnoRepo.findByFechaOrderByHoraInicioAsc(fecha)
            .stream()
            .map(this::toTurnoCliente)  // ⬅️ ya lo tenés
            .toList();
}



private static final int CAPACIDAD_POR_BLOQUE = 2;

private void verificarCapacidadGlobal(LocalDate fecha, LocalTime inicio, LocalTime fin) {
  long ocupados = turnoRepo.countOverlapInFecha(fecha, inicio, fin);
  if (ocupados >= CAPACIDAD_POR_BLOQUE) {
    throw new IllegalStateException("No hay cupo disponible en ese horario");
  }
}

}
