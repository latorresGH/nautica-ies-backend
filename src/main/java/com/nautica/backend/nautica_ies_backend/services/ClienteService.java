package com.nautica.backend.nautica_ies_backend.services;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Admin.Cliente.*;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCliente;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.UsuarioRepository;

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

    public ClienteService(ClienteRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
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
     * Actualiza campos del cliente (versión entidad a entidad).
     */
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente cliente = obtener(id); // 404 si no existe

        cliente.setNumCliente(datos.getNumCliente());
        cliente.setEstadoCliente(datos.getEstadoCliente());
        cliente.setTipoCliente(datos.getTipoCliente());
        cliente.setEmbarcacion(datos.getEmbarcacion());

        try {
            return repo.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Violación de restricción ¿numCliente duplicado?"
            );
        }
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

        c.setEstadoCliente(EstadoCliente.inactivo);
        c.setActivo(false);

        return toDetail(c);
    }

    /* ================== MAPPERS A  ================== */

    private ClienteSummary toSummary(Cliente c) {
        return new ClienteSummary(
                c.getIdUsuario(),            // id heredado de Usuario
                c.getNombre(),
                c.getApellido(),
                c.getCorreo(),
                c.getTelefono(),
                c.getEstadoCliente()
        );
    }

    private ClienteDetail toDetail(Cliente c) {
        Long idEmbarcacion = null;
        Embarcacion emb = c.getEmbarcacion();
        if (emb != null) {
            idEmbarcacion = resolveEmbarcacionId(emb); // adaptá si tu getter se llama distinto
        }

        return new ClienteDetail(
                c.getIdUsuario(),
                c.getNumCliente(),
                c.getTipoCliente() == null ? null : c.getTipoCliente().name(),
                c.getEstadoCliente(),
                c.getNombre(),
                c.getApellido(),
                c.getDni(),
                c.getCorreo(),
                c.getTelefono(),
                c.getDireccion(),
                c.getLocalidad(),
                c.getProvincia(),
                c.getFechaAlta() == null ? null : c.getFechaAlta().toString(),
                idEmbarcacion
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
}
