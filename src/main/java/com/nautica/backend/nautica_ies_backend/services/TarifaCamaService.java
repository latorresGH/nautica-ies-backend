package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas.TarifaCamaDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.TarifasCamas.TarifaCamaRequest;
import com.nautica.backend.nautica_ies_backend.models.TarifaCama;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoCama;
import com.nautica.backend.nautica_ies_backend.repository.TarifaCamaRepository;

@Service
public class TarifaCamaService {

    private final TarifaCamaRepository repo;

    public TarifaCamaService(TarifaCamaRepository repo) {
        this.repo = repo;
    }

    private TarifaCamaDTO toDTO(TarifaCama t) {
        return new TarifaCamaDTO(
                t.getIdTarifa(),
                t.getTipoCama().name(),
                t.getNumeroMes(),
                t.getPrecio()
        );
    }

    /* ===== LISTAR ===== */

    // Listar tarifas de un mes (ej: para la pantalla de admin)
    @Transactional(readOnly = true)
    public List<TarifaCamaDTO> listarPorMes(LocalDate mesParam) {
        LocalDate mes = mesParam.withDayOfMonth(1);
        return repo.findByNumeroMes(mes)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarifaCamaDTO obtenerPorId(Long id) {
        TarifaCama t = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada"));
        return toDTO(t);
    }

    /* ===== CREAR / UPSERT POR TIPO+MES ===== */

    // Crea una tarifa nueva para tipo+mes, o actualiza el precio si ya existe (upsert)
    @Transactional
    public TarifaCamaDTO crearOActualizar(TarifaCamaRequest req) {
        if (req.precio() == null || req.precio().signum() <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        LocalDate mes = req.numeroMes().withDayOfMonth(1);

        TipoCama tipo;
        try {
            // si tu enum está en minúsculas, ajustá acá igual que con FormaPago
            tipo = TipoCama.valueOf(req.tipoCama().trim().toLowerCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de cama inválido: " + req.tipoCama());
        }

        TarifaCama tarifa = repo.findByTipoCamaAndNumeroMes(tipo, mes)
                .orElseGet(() -> {
                    TarifaCama nueva = new TarifaCama();
                    nueva.setTipoCama(tipo);
                    nueva.setNumeroMes(mes);
                    return nueva;
                });

        tarifa.setPrecio(req.precio());

        try {
            TarifaCama guardada = repo.save(tarifa);
            return toDTO(guardada);
        } catch (DataIntegrityViolationException e) {
            // por el unique (tipo_cama, numero_mes)
            throw new IllegalArgumentException("Ya existe una tarifa para ese tipo de cama y mes");
        }
    }

    /* ===== ACTUALIZAR SOLO PRECIO POR ID ===== */

    @Transactional
    public TarifaCamaDTO actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.signum() <= 0) {
            throw new IllegalArgumentException("Precio inválido");
        }

        TarifaCama t = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada"));

        t.setPrecio(nuevoPrecio);
        TarifaCama guardada = repo.save(t);
        return toDTO(guardada);
    }

    /* ===== ELIMINAR (si te interesa) ===== */

    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Tarifa no encontrada");
        }
        repo.deleteById(id);
    }
}
