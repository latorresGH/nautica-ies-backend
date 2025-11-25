package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.models.*;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.models.enums.FormaPago;
import com.nautica.backend.nautica_ies_backend.repository.*;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaResumen;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoCreateRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoSummary;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoDetail;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota.DetalleCuotaEmbarcacion;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota.ResumenCuotaMesCliente;


@Service
public class CuotaService {

    private final CuotaRepository repo;
    private final ClienteRepository clienteRepo;
    private final EmbarcacionRepository embarcacionRepo;
    private final TarifaCamaRepository tarifaRepo;
    private final UsuarioEmbarcacionRepository usuarioEmbRepo;

    public CuotaService(CuotaRepository repo, ClienteRepository clienteRepo, EmbarcacionRepository embarcacionRepo,
            TarifaCamaRepository tarifaRepo, UsuarioEmbarcacionRepository usuarioEmbRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.embarcacionRepo = embarcacionRepo;
        this.tarifaRepo = tarifaRepo;
        this.usuarioEmbRepo = usuarioEmbRepo;
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

    // 👇 NUEVO
    String periodo = String.format("%d-%02d", mes.getYear(), mes.getMonthValue());
    c.setPeriodo(periodo);

    Cliente cliente = clienteRepo.findById(c.getCliente().getIdUsuario())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no existe"));
    Embarcacion emb = embarcacionRepo.findById(c.getEmbarcacion().getIdEmbarcacion())
            .orElseThrow(() -> new ResourceNotFoundException("Embarcación no existe"));

    if (repo.existsByClienteAndEmbarcacionAndNumeroMes(cliente, emb, mes)) {
        throw new IllegalArgumentException("Ya existe una cuota para ese cliente/embarcación/mes");
    }

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
        LocalDate mesNormalizado = datos.getNumeroMes().withDayOfMonth(1);
        c.setNumeroMes(mesNormalizado);

        // 👇 NUEVO: mantener periodo consistente
        String periodo = String.format("%d-%02d", mesNormalizado.getYear(), mesNormalizado.getMonthValue());
        c.setPeriodo(periodo);
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

    public CuotaResumen cuotaActualPorCliente(Long clienteId) {
        return repo.findTopByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId)
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name() // "pendiente" | "pagada" | "vencida"
                ))
                .orElse(null);
    }

    // (Opcional) variante con embarcación
    public CuotaResumen cuotaActualPorClienteYEmbarcacion(Long clienteId, Long embarcacionId) {
        return repo.findTopByCliente_IdUsuarioAndEmbarcacion_IdEmbarcacionOrderByNumeroMesDesc(clienteId, embarcacionId)
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name()))
                .orElse(null);
    }

    // POST /api/pagos
    @Transactional
    public PagoDetail registrarPago(PagoCreateRequest req) {
        Cuota c = obtener(req.cuotaId()); // 404 si no existe

        // Validaciones simples
        if (c.getEstadoCuota() == EstadoCuota.pagada) {
            throw new IllegalArgumentException("La cuota ya se encuentra pagada");
        }

        // Medio (FormaPago) — case-insensitive
        FormaPago medio;
        try {
            medio = FormaPago.valueOf(req.medio().trim().toLowerCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Medio de pago inválido: " + req.medio());
        }

        // Fecha por defecto: hoy
        java.time.LocalDate fecha = (req.fecha() != null) ? req.fecha() : java.time.LocalDate.now();

        // Si viene monto, actualizamos
        if (req.monto() != null) {
            if (req.monto().signum() <= 0)
                throw new IllegalArgumentException("Monto inválido");
            c.setMonto(req.monto());
        }

        c.setFormaPago(medio);
        c.setFechaPago(fecha);
        c.setEstadoCuota(EstadoCuota.pagada);

        // devolvemos detalle
        return toPagoDetail(c);
    }

    // GET /api/pagos
    @Transactional(readOnly = true)
    public Page<PagoSummary> listarPagos(Long clienteId, java.time.LocalDate desde,
            java.time.LocalDate hasta, String medioStr,
            Pageable pageable) {
        FormaPago medio = null;
        if (medioStr != null && !medioStr.isBlank()) {
            try {
                medio = FormaPago.valueOf(medioStr.trim().toLowerCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Medio de pago inválido: " + medioStr);
            }
        }
        return repo.buscarPagos(clienteId, desde, hasta, medio, pageable)
                .map(this::toPagoSummary);
    }

    // GET /api/pagos/{id}
    @Transactional(readOnly = true)
    public PagoDetail obtenerPago(Long id) {
        Cuota c = obtener(id);
        if (c.getEstadoCuota() != EstadoCuota.pagada) {
            throw new ResourceNotFoundException("Pago no encontrado"); // o 404 si no está pagada
        }
        return toPagoDetail(c);
    }

    /* ================== MAPPERS A (PAGOS) ================== */

    private PagoSummary toPagoSummary(Cuota c) {
        Long clienteId = (c.getCliente() != null) ? c.getCliente().getIdUsuario() : null;
        String clienteNombre = (c.getCliente() != null)
                ? (c.getCliente().getNombre() + " " + c.getCliente().getApellido()).trim()
                : null;
        Long embarcacionId = (c.getEmbarcacion() != null) ? c.getEmbarcacion().getIdEmbarcacion() : null;

        return new PagoSummary(
                c.getIdCuota(),
                clienteId,
                clienteNombre,
                embarcacionId,
                c.getNumeroMes(),
                c.getFechaPago(),
                c.getMonto(),
                c.getFormaPago() == null ? null : c.getFormaPago().name());
    }

    private PagoDetail toPagoDetail(Cuota c) {
        Long clienteId = (c.getCliente() != null) ? c.getCliente().getIdUsuario() : null;
        String clienteNombre = (c.getCliente() != null)
                ? (c.getCliente().getNombre() + " " + c.getCliente().getApellido()).trim()
                : null;
        Long embarcacionId = (c.getEmbarcacion() != null) ? c.getEmbarcacion().getIdEmbarcacion() : null;

        return new PagoDetail(
                c.getIdCuota(),
                c.getNumeroPago(),
                clienteId,
                clienteNombre,
                embarcacionId,
                c.getNumeroMes(),
                c.getFechaPago(),
                c.getMonto(),
                c.getFormaPago() == null ? null : c.getFormaPago().name(),
                c.getEstadoCuota() == null ? null : c.getEstadoCuota().name());
    }

    @Transactional(readOnly = true)
    public List<CuotaResumen> listarCuotasCliente(Long clienteId) {
        return repo.findByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId)
                .stream()
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name() // "pendiente", "pagada", "vencida"
                ))
                .toList();
    }

    // resumen simple de deuda para el cliente
    @Transactional(readOnly = true)
    public DeudaCliente resumenDeudaCliente(Long clienteId) {
        var cuotas = repo.findByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId);

        long cuotasImpagas = cuotas.stream()
                .filter(c -> c.getEstadoCuota() != EstadoCuota.pagada)
                .count();

        BigDecimal totalImpago = cuotas.stream()
                .filter(c -> c.getEstadoCuota() != EstadoCuota.pagada)
                .map(Cuota::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DeudaCliente(cuotasImpagas, totalImpago);
    }

    // Podés meter esto como record estático o clase simple
    public static record DeudaCliente(long cuotasImpagas, BigDecimal totalImpago) {
    }

    // Ejemplo: obtener precio para una embarcación en un mes
    private BigDecimal obtenerPrecioMesParaEmbarcacion(Embarcacion emb, LocalDate mes) {
        if (emb.getTipoCama() == null) {
            throw new IllegalArgumentException("La embarcación " + emb.getIdEmbarcacion()
                    + " no tiene tipo de cama asignado");
        }

        LocalDate mesNormalizado = mes.withDayOfMonth(1);

        TarifaCama tarifa = tarifaRepo.findByTipoCamaAndNumeroMes(emb.getTipoCama(), mesNormalizado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay tarifa definida para tipo de cama " + emb.getTipoCama()
                                + " en el mes " + mesNormalizado));

        return tarifa.getPrecio();
    }

    /**
     * Genera cuotas para TODAS las embarcaciones cuyo usuario sea PROPIETARIO
     * en el mes dado.
     *
     * - mesParam: cualquier día del mes -> se normaliza al día 1
     * - No pisa cuotas existentes (cliente + embarcación + numeroMes únicos)
     * - Usa obtenerPrecioMesParaEmbarcacion(...)
     * - Devuelve cuántas cuotas nuevas creó
     */
    @Transactional
    public int generarCuotasMes(LocalDate mesParam) {
        LocalDate mes = mesParam.withDayOfMonth(1); // normalizamos al 1

        // Todas las relaciones propietario-embarcación activas
        var relaciones = usuarioEmbRepo.findByRolEnEmbarcacionAndHastaIsNull(RolEnEmbarcacion.propietario);

        int creadas = 0;

        for (UsuarioEmbarcacion ue : relaciones) {
            var usuario = ue.getUsuario();
            var embarcacion = ue.getEmbarcacion();

            if (usuario == null || embarcacion == null) {
                continue;
            }

            // Buscamos el "Cliente" que corresponde al usuario
            // (asumiendo que Cliente.id = Usuario.id)
            Long clienteId = usuario.getIdUsuario();
            Cliente cliente = clienteRepo.findById(clienteId)
                    .orElse(null);

            if (cliente == null) {
                // si no hay cliente, no generamos cuota
                continue;
            }

            // Si ya existe una cuota para este cliente + embarcación + mes -> skip
            if (repo.existsByClienteAndEmbarcacionAndNumeroMes(cliente, embarcacion, mes)) {
                continue;
            }

            // Obtengo el precio según tipo de cama y mes
            BigDecimal precio = obtenerPrecioMesParaEmbarcacion(embarcacion, mes);

            // Numero de pago correlativo para ese cliente+embarcación
            int nextNumero = repo.findTopByClienteAndEmbarcacionOrderByNumeroPagoDesc(cliente, embarcacion)
                    .map(c -> c.getNumeroPago() + 1)
                    .orElse(1);

            String periodo = String.format("%d-%02d", mes.getYear(), mes.getMonthValue());

            Cuota cuota = new Cuota();
            cuota.setCliente(cliente);
            cuota.setEmbarcacion(embarcacion);
            cuota.setNumeroMes(mes);
            cuota.setPeriodo(periodo);
            cuota.setNumeroPago(nextNumero);
            cuota.setMonto(precio);
            cuota.setEstadoCuota(EstadoCuota.pendiente);
            cuota.setFechaPago(null);
            cuota.setFormaPago(null); // se setea cuando efectivamente se paga

            repo.save(cuota);
            creadas++;
        }

        return creadas;
    }

    @Transactional(readOnly = true)
public ResumenCuotaMesCliente resumenCuotaMesCliente(Long clienteId, LocalDate mesParam) {
    LocalDate mes = mesParam.withDayOfMonth(1);

    var cuotasMes = repo.findByCliente_IdUsuarioAndNumeroMes(clienteId, mes);
    if (cuotasMes.isEmpty()) {
        return null;
    }

    var total = cuotasMes.stream()
            .map(Cuota::getMonto)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

    String periodo = cuotasMes.get(0).getPeriodo(); // todas comparten el mismo

    var detalles = cuotasMes.stream()
            .map(c -> new DetalleCuotaEmbarcacion(
                    c.getEmbarcacion().getIdEmbarcacion(),
                    c.getEmbarcacion().getNombre(),
                    c.getEmbarcacion().getTipoCama(),
                    c.getMonto()
            ))
            .toList();

    return new ResumenCuotaMesCliente(mes, periodo, total, detalles);
}

}
