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
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;
import com.nautica.backend.nautica_ies_backend.repository.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaResumen;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.PagoCuotasRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota.DetalleCuotaEmbarcacion;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cuota.ResumenCuotaMesCliente;

@Service
public class CuotaService {

    private final CuotaRepository repo;
    private final ClienteRepository clienteRepo;
    private final EmbarcacionRepository embarcacionRepo;
    private final TarifaCamaRepository tarifaRepo;
    private final UsuarioEmbarcacionRepository usuarioEmbRepo;

    public CuotaService(
            CuotaRepository repo,
            ClienteRepository clienteRepo,
            EmbarcacionRepository embarcacionRepo,
            TarifaCamaRepository tarifaRepo,
            UsuarioEmbarcacionRepository usuarioEmbRepo
    ) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.embarcacionRepo = embarcacionRepo;
        this.tarifaRepo = tarifaRepo;
        this.usuarioEmbRepo = usuarioEmbRepo;
    }

    /* ===========================================================
     * CRUD básico de Cuota
     * =========================================================== */

    public Page<Cuota> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    public Cuota obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuota no encontrada"));
    }

    @Transactional
    public Cuota crear(Cuota c) {
        // normalizamos numeroMes al día 1
        LocalDate mes = c.getNumeroMes().withDayOfMonth(1);
        c.setNumeroMes(mes);

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

    /* ===========================================================
     * Cuotas: resumen cliente
     * =========================================================== */

    public CuotaResumen cuotaActualPorCliente(Long clienteId) {
        return repo.findTopByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId)
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name()
                ))
                .orElse(null);
    }

    public CuotaResumen cuotaActualPorClienteYEmbarcacion(Long clienteId, Long embarcacionId) {
        return repo.findTopByCliente_IdUsuarioAndEmbarcacion_IdEmbarcacionOrderByNumeroMesDesc(clienteId, embarcacionId)
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CuotaResumen> listarCuotasCliente(Long clienteId) {
        return repo.findByCliente_IdUsuarioOrderByNumeroMesDesc(clienteId)
                .stream()
                .map(c -> new CuotaResumen(
                        c.getNumeroMes(),
                        c.getMonto(),
                        c.getEstadoCuota().name()
                ))
                .toList();
    }

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

    public static record DeudaCliente(long cuotasImpagas, BigDecimal totalImpago) {
    }

    /* ===========================================================
     * Tarifas + generación mensual
     * =========================================================== */

    private BigDecimal obtenerPrecioMesParaEmbarcacion(Embarcacion emb, LocalDate mes) {
        if (emb.getTipoCama() == null) {
            throw new IllegalArgumentException("La embarcación " + emb.getIdEmbarcacion()
                    + " no tiene tipo de cama asignado");
        }

        LocalDate mesNormalizado = mes.withDayOfMonth(1);

        TarifaCama tarifa = tarifaRepo
                .findTopByTipoCamaAndNumeroMesLessThanEqualOrderByNumeroMesDesc(
                        emb.getTipoCama(),
                        mesNormalizado
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay ninguna tarifa definida para tipo de cama " + emb.getTipoCama()
                                + " en o antes del mes " + mesNormalizado));

        return tarifa.getPrecio();
    }

    @Transactional
    public int generarCuotasMes(LocalDate mesParam) {
        LocalDate mes = mesParam.withDayOfMonth(1); // normalizamos al 1

        var relaciones = usuarioEmbRepo.findByRolEnEmbarcacionAndHastaIsNull(RolEnEmbarcacion.propietario);

        int creadas = 0;

        for (UsuarioEmbarcacion ue : relaciones) {
            var usuario = ue.getUsuario();
            var embarcacion = ue.getEmbarcacion();

            if (usuario == null || embarcacion == null) {
                continue;
            }

            Long clienteId = usuario.getIdUsuario();
            Cliente cliente = clienteRepo.findById(clienteId)
                    .orElse(null);

            if (cliente == null) {
                continue;
            }

            if (repo.existsByClienteAndEmbarcacionAndNumeroMes(cliente, embarcacion, mes)) {
                continue;
            }

            BigDecimal precio = obtenerPrecioMesParaEmbarcacion(embarcacion, mes);

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
            cuota.setFormaPago(null);

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
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String periodo = cuotasMes.get(0).getPeriodo();

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

    /* ===========================================================
     *  NUEVO: lógica para el panel ADMIN de pagos
     * =========================================================== */

    private FormaPago parseFormaPago(String medioStr) {
        if (medioStr == null || medioStr.isBlank()) {
            throw new IllegalArgumentException("Medio de pago obligatorio");
        }
        try {
            return FormaPago.valueOf(medioStr.trim().toLowerCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Medio de pago inválido: " + medioStr);
        }
    }

    /**
     * Lista de cuotas IMPAGAS (pendientes o vencidas) con datos de embarcación.
     */
        @Transactional(readOnly = true)
    public List<CuotaAdminDTO> listarCuotasImpagasPorCliente(Long clienteId) {
        var estadosImpagos = List.of(EstadoCuota.pendiente, EstadoCuota.vencida);

        return repo
                .findByCliente_IdUsuarioAndEstadoCuotaInOrderByNumeroMesAsc(clienteId, estadosImpagos)
                .stream()
                .map(c -> {
                    Embarcacion e = c.getEmbarcacion();   // puede venir null si hay datos viejos o rotos

                    Long idEmb = (e != null) ? e.getIdEmbarcacion() : null;
                    String nombreEmb = (e != null) ? e.getNombre() : null;
                    String matriculaEmb = (e != null) ? e.getNumMatricula() : null;

                    return new CuotaAdminDTO(
                            c.getIdCuota(),
                            c.getNumeroMes(),     // LocalDate, puede ser null y no pasa nada
                            c.getPeriodo(),       // String, puede ser null para cuotas viejas
                            c.getMonto(),
                            c.getEstadoCuota(),
                            idEmb,
                            nombreEmb,
                            matriculaEmb
                    );
                })
                .toList();
    }


    /**
     * Marca como PAGADAS las cuotas indicadas en el request.
     */
    @Transactional
    public void registrarPagoCuotas(PagoCuotasRequest req) {
        if (req.cuotasIds() == null || req.cuotasIds().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una cuota.");
        }

        var cuotas = repo.findByIdCuotaIn(req.cuotasIds());
        if (cuotas.size() != req.cuotasIds().size()) {
            throw new ResourceNotFoundException("Alguna de las cuotas seleccionadas no existe.");
        }

        LocalDate fecha = (req.fecha() != null) ? req.fecha() : LocalDate.now();
        FormaPago medio = parseFormaPago(req.medio());

        for (Cuota c : cuotas) {
            if (c.getCliente() == null || !c.getCliente().getIdUsuario().equals(req.clienteId())) {
                throw new IllegalArgumentException(
                        "La cuota " + c.getIdCuota() + " no pertenece al cliente indicado."
                );
            }
            if (c.getEstadoCuota() == EstadoCuota.pagada) {
                throw new IllegalArgumentException(
                        "La cuota " + c.getIdCuota() + " ya está marcada como pagada."
                );
            }

            c.setEstadoCuota(EstadoCuota.pagada);
            c.setFechaPago(fecha);
            c.setFormaPago(medio);
        }

        repo.saveAll(cuotas);
    }
}
