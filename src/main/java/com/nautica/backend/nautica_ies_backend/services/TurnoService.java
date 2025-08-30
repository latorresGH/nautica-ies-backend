package com.nautica.backend.nautica_ies_backend.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.repository.TurnoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TurnoService {

  private final TurnoRepository turnoRepo;

  public TurnoService(TurnoRepository turnoRepo) {
    this.turnoRepo = turnoRepo;
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
}
