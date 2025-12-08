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
import com.nautica.backend.nautica_ies_backend.models.enums.TipoTarea;

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
  verificarCapacidadGlobal(turno.getFecha(), turno.getHoraInicio());
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
      throw new IllegalArgumentException("TURNOS_HORA_POSTERIOR");
  }

private void verificarSolapamiento(
    Long idTurnoExcluido,
    LocalDate fecha,
    LocalTime inicio,
    LocalTime fin,
    Long idEmbarcacion
) {
    // Solo turnos de esa embarcación y ese día
    List<Turno> turnos =
        turnoRepo.findByFechaAndEmbarcacion_IdEmbarcacion(fecha, idEmbarcacion);

    for (Turno t : turnos) {

        // si estoy editando un turno, lo excluyo
        if (idTurnoExcluido != null && t.getIdTurno().equals(idTurnoExcluido)) {
            continue;
        }

        // 👇 Buscar la tarea asociada y saltar las canceladas
        var tareaOpt = tareaRepo.findByTurno_Id(t.getIdTurno());
        if (tareaOpt.isPresent() && tareaOpt.get().getEstado() == EstadoTarea.cancelado) {
            continue; // no bloquea nada
        }

        LocalTime iniExist = t.getHoraInicio();
        LocalTime finExistConBuffer = t.getHoraFin().plusHours(1); // buffer de 1 hora

        boolean seSolapan = inicio.isBefore(finExistConBuffer) && fin.isAfter(iniExist);

        if (seSolapan) {
            throw new IllegalStateException("TURNOS_EMBARCACION_SOLAPADOS");
        }
    }
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

@Transactional
public TurnoCliente solicitarTurno(TurnoSolicitudRequest req) {
  // buscar cliente y embarcación
  Cliente cliente = clienteRepo.findById(req.clienteId())
      .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

  Embarcacion emb = embarRepo.findById(req.idEmbarcacion())
      .orElseThrow(() -> new EntityNotFoundException("Embarcación no encontrada"));

  // 1) validar que vengan ambas horas
  if (req.horaInicio() == null || req.horaFin() == null) {
    throw new IllegalArgumentException("TURNOS_HORAS_OBLIGATORIAS");
  }

  // 2) crear Turno con el rango COMPLETO que manda el front
  Turno t = new Turno();
  t.setCliente(cliente);
  t.setEmbarcacion(emb);
  t.setFecha(req.fecha());
  t.setHoraInicio(req.horaInicio());
  t.setHoraFin(req.horaFin());   // 👈 ACÁ SE RESPETA LO DEL FRONT

  // 👉 valida que fin sea posterior a inicio
validarHoras(t.getHoraInicio(), t.getHoraFin());

  // 👉 valida solapamiento para ESA embarcación
verificarSolapamiento(
    null,
    t.getFecha(),
    t.getHoraInicio(),
    t.getHoraFin(),
    t.getEmbarcacion().getIdEmbarcacion()
);

  // 👉 valida capacidad global (2 a la vez, si lo tenés activo)
  verificarCapacidadGlobal(t.getFecha(), t.getHoraInicio());

  Turno guardado = turnoRepo.save(t);

  // 3) crear Tarea asociada
  Tarea tarea = new Tarea();
  tarea.setTurno(guardado);
  tarea.setFecha(guardado.getFecha());
  tarea.setHora(guardado.getHoraInicio());
  tarea.setEstado(EstadoTarea.pendiente);

  // 🔹 tipo tarea desde el request ("lavado" | "botado" → enum)
  TipoTarea tipoEnum = mapTipoTareaFromRequest(req.tipo());
  tarea.setTipoTarea(tipoEnum);

  // 🔹 número de tarea autoincremental
  Integer nextNumero = tareaRepo.findTopByOrderByNumeroTareaDesc()
      .map(Tarea::getNumeroTarea)
      .map(n -> n + 1)
      .orElse(1);
  tarea.setNumeroTarea(nextNumero);

  // 🔹 sin operario asignado por ahora
  tarea.setOperario(null);

  tareaRepo.save(tarea);

  return toTurnoCliente(guardado);
}


private TipoTarea mapTipoTareaFromRequest(String raw) {
  if (raw == null || raw.isBlank()) {
    throw new IllegalArgumentException("TURNOS_TIPO_OBLIGATORIO");
  }

  switch (raw.toLowerCase()) {
    case "lavado":
      // 👇 usa EXACTAMENTE el nombre de la constante de tu enum
      return TipoTarea.lavado; // o TipoTarea.LAVADO_EMBARCACION, etc.
    case "botado":
      return TipoTarea.botado; // o TipoTarea.BOTADO_EMBARCACION, etc.
    default:
      throw new IllegalArgumentException("TURNOS_TIPO_INVALIDO");
  }
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
    .map(t -> {
      if (t.getTipoTarea() == null) return null;
      // lo podés mapear explícitamente o simplemente .name().toLowerCase()
      return t.getTipoTarea().name().toLowerCase();
    })
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

private void verificarCapacidadGlobal(LocalDate fecha, LocalTime inicio) {
    // Normalizar al bloque de 15' (por si en algún momento llega algo raro)
    int minuto = inicio.getMinute();
    int bloque = (minuto / 15) * 15; // 0, 15, 30, 45

    LocalTime bloqueInicio = inicio
            .withMinute(bloque)
            .withSecond(0)
            .withNano(0);

    // ⬇️ contamos SOLO tareas que NO estén canceladas
    long ocupados = tareaRepo.countByFechaAndHoraAndEstadoNot(
            fecha,
            bloqueInicio,
            EstadoTarea.cancelado
    );

    if (ocupados >= CAPACIDAD_POR_BLOQUE) {
        throw new IllegalStateException("TURNOS_CAP_GLOBAL");
    }
}




}
