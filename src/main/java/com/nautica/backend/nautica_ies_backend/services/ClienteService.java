package com.nautica.backend.nautica_ies_backend.services;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Asegúrate de importar esto
import org.springframework.stereotype.Repository;
import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente.*;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Resumen.ClienteAdminResumenDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Cliente.ClientePatchRequest;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCuota;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.EmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioEmbarcacionRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;
import com.nautica.backend.nautica_ies_backend.repository.CuotaRepository;

//imports para Cliente y Embarcaciones del admin
import com.nautica.backend.nautica_ies_backend.services.UsuarioEmbarcacionService;
import com.nautica.backend.nautica_ies_backend.services.CuotaService;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Resumen.EmbarcacionAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Pagos.CuotaResumen;
import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;


/**
 * Servicio encargado de la lógica de negocio relacionada con {@link Cliente}.
 * Mantiene los métodos existentes (listar/crear/obtener/actualizar/eliminar)
 * y agrega métodos orientados a los endpoints ADMIN con s y búsqueda.
 */
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final EmbarcacionRepository embarcacionRepo;
    private final UsuarioEmbarcacionRepository ueRepo;
    private final CuotaRepository cuotaRepo;
    private final UsuarioEmbarcacionService usuarioEmbarcacionService;
    private final CuotaService cuotaService;


    public ClienteService(ClienteRepository repo, UsuarioRepository usuarioRepo, EmbarcacionRepository embarcacionRepo, UsuarioEmbarcacionRepository ueRepo, CuotaRepository cuotaRepo,
        UsuarioEmbarcacionService usuarioEmbarcacionService, CuotaService cuotaService) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.embarcacionRepo = embarcacionRepo;
        this.ueRepo = ueRepo;
        this.cuotaRepo = cuotaRepo;
        this.usuarioEmbarcacionService = usuarioEmbarcacionService;
        this.cuotaService = cuotaService;
    }

    /* ===========================================================
     * ===========   MÉTODOS EXISTENTES (se mantienen)   =========
     * =========================================================== */

    /**
     * Lista los clientes de forma paginada y ordenada.
     */
    @Transactional(readOnly = true)
    public Page<Cliente> listar(int page, int size, Sort sort) {
        return repo.findAll(PageRequest.of(page, size, sort));
    }

    /**
     * Lista todos los clientes.
     */
    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return repo.findAll();
    }

    /**
     * Crea un nuevo cliente.
     */
    public Cliente crear(Cliente cliente) {
        try {
            return repo.save(cliente);
        } catch (DataIntegrityViolationException e) {
            // num_cliente UNIQUE u otras restricciones
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Violación de restricción ¿numCliente duplicado?"
            );
        }
    }

    /**
     * Obtiene un cliente por ID (entidad).
     */
    @Transactional(readOnly = true)
    public Cliente obtener(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    /**
     *  Actualiza parcialmente un cliente con los campos no nulos del request.
     */
public Cliente actualizarParcial(Long id, ClientePatchRequest req) {
    Cliente c = obtener(id); // 404 si no existe

    if (req.nombre()    != null) c.setNombre(req.nombre().trim());
    if (req.apellido()  != null) c.setApellido(req.apellido().trim());

    if (req.correo()    != null) {
        String nuevo = req.correo().trim().toLowerCase();
        // si tenés UsuarioRepository inyectado, validamos unicidad:
        if (usuarioRepo != null && c.getCorreo() != null &&
            !c.getCorreo().equalsIgnoreCase(nuevo) &&
            usuarioRepo.existsByCorreoIgnoreCase(nuevo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo ya usado por otro usuario");
        }
        c.setCorreo(nuevo);
    }

    if (req.telefono()  != null) c.setTelefono(req.telefono());
    if (req.direccion() != null) c.setDireccion(req.direccion());
    if (req.localidad() != null) c.setLocalidad(req.localidad());
    if (req.provincia() != null) c.setProvincia(req.provincia());

    return repo.save(c);
}


    /**
     * Elimina un cliente por ID.
     */
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }
        repo.deleteById(id);
    }

    /**
     * Busca por número de cliente.
     */
    @Transactional(readOnly = true)
    public Cliente buscarPorNumero(Integer numCliente) {
        return repo.findByNumCliente(numCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }

    /**
     * Conteos (reutiliza tu repo).
     */
    @Transactional(readOnly = true)
    public long contarTodos() {
        return repo.count();
    }

    @Transactional(readOnly = true)
    public long contarActivos() {
        return repo.countByActivoTrue();
    }

    /* =================================================================
     * ===========   NUEVOS MÉTODOS PARA ENDPOINTS ADMIN () =========
     * ================================================================= */

    /**
     * Listado ADMIN con búsqueda por nombre/apellido/correo/telefono/dni.
     * Usa el método repo.buscar(q, pageable).
     */
    @Transactional(readOnly = true)
    public Page<ClienteSummary> listarAdmin(String buscar, Pageable pageable) {
        return repo.buscar(buscar, pageable).map(this::toSummary);
    }

    /**
     * Detalle ADMIN () por ID.
     */
    @Transactional(readOnly = true)
    public ClienteDetail obtenerAdmin(Long id) {
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        return toDetail(c);
    }

    /**
     * Edición de datos personales/contacto del cliente (ADMIN) vía .
     * No toca estado/tipo/numCliente acá.
     */
    public ClienteDetail editarAdmin(Long id, ClienteUpdateRequest req) {
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // Chequeo de correo único solo si cambió
        String correoActual = c.getCorreo();
        if (correoActual != null && !correoActual.equalsIgnoreCase(req.correo())
                && usuarioRepo.existsByCorreoIgnoreCase(req.correo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo ya usado por otro usuario");
        }

        c.setNombre(req.nombre().trim());
        c.setApellido(req.apellido().trim());
        c.setCorreo(req.correo().trim().toLowerCase());
        c.setTelefono(req.telefono());
        c.setDireccion(req.direccion());
        c.setLocalidad(req.localidad());
        c.setProvincia(req.provincia());

        return toDetail(c);
    }

    /**
     * Baja lógica (ADMIN): estado_cliente=INACTIVO y activo=false.
     * Sin body adicional.
     */
    public ClienteDetail bajaAdmin(Long id) {
        var c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        c.setActivo(false);
        return toDetail(c);
    }


    /* ================== MAPPERS A  ================== */

private ClienteSummary toSummary(Cliente c) {
    return new ClienteSummary(
        c.getIdUsuario(),                 // si usás getIdUsuario() por herencia
        c.getNombre(),
        c.getApellido(),
        c.getCorreo(),
        c.getTelefono(),
        c.getActivo()                     // <— acá
    );
}


    private ClienteDetail toDetail(Cliente c) {
    return new ClienteDetail(
        c.getIdUsuario(),
        c.getNumCliente(),
        c.getTipoCliente() == null ? null : c.getTipoCliente().name(),
        c.getActivo(),                    // <— acá
        c.getNombre(),
        c.getApellido(),
        c.getDni(),
        c.getCorreo(),
        c.getTelefono(),
        c.getDireccion(),
        c.getLocalidad(),
        c.getProvincia(),
        c.getFechaAlta() == null ? null : c.getFechaAlta().toString(),
        c.getEmbarcacion() == null ? null : c.getEmbarcacion().getIdEmbarcacion()
    );
    }

    /**
     * Intenta obtener el ID de Embarcacion llamando a getIdEmbarcacion() o getId().
     * Cambiá este método si tu entidad usa otro nombre fijo.
     */
    private Long resolveEmbarcacionId(Embarcacion e) {
        try {
            Method m = e.getClass().getMethod("getIdEmbarcacion");
            Object val = m.invoke(e);
            return (val instanceof Long) ? (Long) val : null;
        } catch (Exception ignore) {
            try {
                Method m = e.getClass().getMethod("getId");
                Object val = m.invoke(e);
                return (val instanceof Long) ? (Long) val : null;
            } catch (Exception ignore2) {
                return null;
            }
        }
        
    }

      /**
     * Método para buscar el usuario por correo
     */
    public Usuario buscarPorCorreo(String correo) {
        // Usar Optional para obtener el usuario
        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);

        // Si el usuario está presente, lo devolvemos, si no, lanzamos una excepción
        return usuarioOpt.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para el correo: " + correo));
    }



    /**
 * Baja definitiva del CLIENTE (elimina fila en `clientes`),
 * borra relaciones a embarcaciones y elimina embarcaciones que queden sin dueños,
 * pero mantiene `usuario` con activo=false.
 * Falla si hay cuotas en deuda (pendiente/vencida).
 */
@Transactional
public void bajaDefinitivaSiSinDeuda(Long id) {
    var cliente = obtener(id); // 404 si no existe

    // 1) Bloquear si hay deuda
    var estadosBloqueo = java.util.List.of(EstadoCuota.pendiente, EstadoCuota.vencida);
    long deudas = cuotaRepo.countByCliente_IdUsuarioAndEstadoCuotaIn(id, estadosBloqueo);
    if (deudas > 0) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
            "No se puede dar de baja: el cliente tiene cuotas pendientes/vencidas");
    }

    // 1.b) Eliminar TODAS las cuotas (pagadas o no) del cliente
    cuotaRepo.deleteByCliente_IdUsuario(id);

    // 2) Capturar embarcaciones asociadas para evaluar orfandad
    var links = ueRepo.findByUsuario_IdUsuario(id);
    var posiblesOrfanas = new java.util.HashSet<Long>();
    for (var ue : links) {
        if (ue.getEmbarcacion() != null) {
            posiblesOrfanas.add(ue.getEmbarcacion().getIdEmbarcacion());
        }
    }

    // 3) Borrar relaciones usuario_embarcaciones del cliente
    ueRepo.deleteByUsuario_IdUsuario(id);

    // 4) Eliminar embarcaciones que quedaron sin dueños
    var aEliminar = new java.util.ArrayList<Long>();
    for (Long embId : posiblesOrfanas) {
        long cnt = ueRepo.countByEmbarcacion_IdEmbarcacion(embId);
        if (cnt == 0) aEliminar.add(embId);
    }
    if (!aEliminar.isEmpty()) {
        embarcacionRepo.deleteByIdEmbarcacionIn(aEliminar);
    }

    // 5) Marcar usuario inactivo (se conserva la fila en `usuarios`)
    cliente.setActivo(false);

    // 6) Eliminar SOLO la fila de `clientes` (desaparece de listados de clientes)
    repo.deleteRowOnlyFromClientes(id);
}

    /**
     * Listado ADMIN en formato ClienteAdminResumenDTO.
     * 
     */
    public Page<ClienteAdminResumenDTO> listarAdminResumen(String buscar, Pageable pageable) {
    Page<ClienteSummary> base = listarAdmin(buscar, pageable);

        return base.map(c -> {
            // 1) relaciones usuario-embarcación para este cliente
            List<UsuarioEmbarcacion> relaciones = usuarioEmbarcacionService.listarPorUsuario(c.id());

            // 2) mapear embarcaciones a DTO admin (id, nombre, matrícula)
            List<EmbarcacionAdminDTO> embarcaciones = relaciones.stream()
                    .map(rel -> {
                        Embarcacion e = rel.getEmbarcacion();
                        return new EmbarcacionAdminDTO(
                                e.getIdEmbarcacion(),
                                e.getNombre(),
                                e.getNumMatricula() 
                        );
                    })
                    .toList();

            // 3) calcular si tiene deuda en alguna embarcación
            boolean conDeuda = false;

            for (EmbarcacionAdminDTO eDto : embarcaciones) {
                CuotaResumen cuota = cuotaService.cuotaActualPorClienteYEmbarcacion(
                        c.id(),
                        eDto.id()
                );

                if (cuota != null && !"PAGADA".equalsIgnoreCase(cuota.estado())) {
                    conDeuda = true;
                    break;
                }
            }

            String estadoCuotas = conDeuda ? "CON_DEUDA" : "AL_DIA";

            // Buscar el usuario para leer el estado real de la tabla usuarios
            boolean activoUsuario = usuarioRepo.findById(c.id())
                    .map(u -> Boolean.TRUE.equals(u.getActivo()))
                    .orElse(false);

            return new ClienteAdminResumenDTO(
                    c.id(),
                    c.nombre(),
                    c.apellido(),
                    c.telefono(),
                    activoUsuario,
                    embarcaciones,
                    estadoCuotas
            );

        });
    }

    @Transactional(readOnly = true)
public ClienteInfoAdminDTO obtenerInfoAdmin(Long idCliente) {
    Cliente c = obtener(idCliente); // ya lo tenés

    // 1) Embarcaciones del cliente (similar a listarAdminResumen)
    List<UsuarioEmbarcacion> relaciones = usuarioEmbarcacionService.listarPorUsuario(idCliente);

    var embarcaciones = relaciones.stream()
    .map(rel -> {
        Embarcacion e = rel.getEmbarcacion();

        return new EmbarcacionClienteInfoDTO(
            e.getIdEmbarcacion(),
            e.getNombre(),
            e.getNumMatricula(),
            e.getTipoCama() != null ? e.getTipoCama().name() : null
        );

    })
    .toList();


    // 2) Personas autorizadas (otros usuarios con la misma embarcación)
    //    Esto es un borrador: después ajustamos la lógica de rol si hace falta.
    var autorizados = embarcaciones.stream()
            .flatMap(eDto -> {
                Long embId = eDto.id();
                var links = ueRepo.findByEmbarcacion_IdEmbarcacion(embId);

                return links.stream()
                        .filter(ue -> !ue.getUsuario().getIdUsuario().equals(idCliente)) // excluye al dueño
                        .map(ue -> {
                            var u = ue.getUsuario();
                            return new PersonaAutorizadaDTO(
                                    u.getIdUsuario(),
                                    u.getNombre(),
                                    u.getApellido(),
                                    ue.getRolEnEmbarcacion() != null ? ue.getRolEnEmbarcacion().name() : null,
                                    embId,
                                    eDto.nombre(),
                                    eDto.matricula()
                            );
                        });
            })
            .distinct()
            .toList();

    // 3) Estado de cuenta
    var estadosDeuda = java.util.List.of(EstadoCuota.pendiente, EstadoCuota.vencida);
    long cuotasAdeudadas = cuotaRepo.countByCliente_IdUsuarioAndEstadoCuotaIn(idCliente, estadosDeuda);
    java.math.BigDecimal montoAdeudado = cuotaRepo.sumDeudaPorClienteYEstados(idCliente, estadosDeuda);

    String estadoCuotas = (cuotasAdeudadas > 0) ? "CON_DEUDA" : "AL_DIA";

    ClienteEstadoCuentaAdminDTO estadoCuenta = new ClienteEstadoCuentaAdminDTO(
            estadoCuotas,
            cuotasAdeudadas,
            montoAdeudado
    );

    // 4) Armar DTO principal
    return new ClienteInfoAdminDTO(
            c.getIdUsuario(),
            c.getNombre(),
            c.getApellido(),
            c.getDni(),
            c.getCorreo(),
            c.getTelefono(),
            c.getDireccion(),
            c.getLocalidad(),
            c.getProvincia(),
            Boolean.TRUE.equals(c.getActivo()),
            embarcaciones,
            autorizados,
            estadoCuenta
    );
}

}
