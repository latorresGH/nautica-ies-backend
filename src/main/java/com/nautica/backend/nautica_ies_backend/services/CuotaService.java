package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.CuotaResumenDTO;
import com.nautica.backend.nautica_ies_backend.models.*;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.repository.*;

@Service
public class CuotaService {

    private final CuotaRepository repo;
    private final ClienteRepository clienteRepo;
    private final EmbarcacionRepository embarcacionRepo;

    public CuotaService(CuotaRepository repo, ClienteRepository clienteRepo, EmbarcacionRepository embarcacionRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.embarcacionRepo = embarcacionRepo;
    }

    public Page<Cuota> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Cuota obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada"));
    }

    @Transactional
    public Cuota crear(Cuota c) {
        // normalizamos numeroMes al día 1
        LocalDate mes = c.getNumeroMes().withDayOfMonth(1);
        c.setNumeroMes(mes);

        Cliente cliente = clienteRepo.findById(c.getCliente().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no existe"));
        Embarcacion emb = embarcacionRepo.findById(c.getEmbarcacion().getIdEmbarcacion())
                .orElseThrow(() -> new ResourceNotFoundException("Embarcación no existe"));

        if (repo.existsByClienteAndEmbarcacionAndNumeroMes(cliente, emb, mes)) {
            throw new IllegalArgumentException("Ya existe una cuota para ese cliente/embarcación/mes");
        }

        // numero_pago correlativo
        int next = repo.findTopByClienteAndEmbarcacionOrderByNumeroPagoDesc(cliente, emb)
                .map(x -> x.getNumeroPago() + 1)
                .orElse(1);
        c.setNumeroPago(next);

        c.setCliente(cliente);
        c.setEmbarcacion(emb);

        if (c.getEstadoCuota() == null)
            c.setEstadoCuota(EstadoCuota.pendiente);
        if (c.getMonto() == null || c.getMonto().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Monto inválido");

        try {
            return repo.save(c);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Violación de restricciones al crear la cuota");
        }
    }

    @Transactional
    public Cuota actualizar(Long id, Cuota datos) {
        Cuota c = obtener(id);

        if (datos.getNumeroMes() != null) {
            c.setNumeroMes(datos.getNumeroMes().withDayOfMonth(1));
        }
        if (datos.getMonto() != null)
            c.setMonto(datos.getMonto());
        if (datos.getFechaPago() != null)
            c.setFechaPago(datos.getFechaPago());
        if (datos.getEstadoCuota() != null)
            c.setEstadoCuota(datos.getEstadoCuota());
        if (datos.getFormaPago() != null)
            c.setFormaPago(datos.getFormaPago());

        // (opcional) cambio de cliente/embarcación: validar y actualizar
        if (datos.getCliente() != null && datos.getCliente().getIdUsuario() != null) {
            Cliente cl = clienteRepo.findById(datos.getCliente().getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no existe"));
            c.setCliente(cl);
        }
        if (datos.getEmbarcacion() != null && datos.getEmbarcacion().getIdEmbarcacion() != null) {
            Embarcacion em = embarcacionRepo.findById(datos.getEmbarcacion().getIdEmbarcacion())
                    .orElseThrow(() -> new ResourceNotFoundException("Embarcación no existe"));
            c.setEmbarcacion(em);
        }

        try {
            return repo.save(c);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Ya existe una cuota para ese cliente/embarcación/mes");
        }
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Cuota no encontrada");
        repo.deleteById(id);
    }

public CuotaResumenDTO cuotaActualPorCliente(Long clienteId) {
    return repo.findTopByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId)
        .map(c -> new CuotaResumenDTO(
            c.getNumeroMes(),
            c.getMonto(),
            c.getEstadoCuota().name() // "pendiente" | "pagada" | "vencida"
        ))
        .orElse(null);
}

// (Opcional) variante con embarcación
public CuotaResumenDTO cuotaActualPorClienteYEmbarcacion(Long clienteId, Long embarcacionId) {
    return repo.findTopByCliente_IdUsuarioAndEmbarcacion_IdEmbarcacionOrderByNumeroMesDesc(clienteId, embarcacionId)
        .map(c -> new CuotaResumenDTO(
            c.getNumeroMes(),
            c.getMonto(),
            c.getEstadoCuota().name()
        ))
        .orElse(null);
}
}
