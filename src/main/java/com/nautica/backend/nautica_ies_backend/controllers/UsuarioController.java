// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/UsuarioController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.UsuarioCreateRequest;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.Administrador;
import com.nautica.backend.nautica_ies_backend.models.Usuario;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoCliente;
import com.nautica.backend.nautica_ies_backend.models.enums.RolUsuario;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoAdministrador;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoCliente;
import com.nautica.backend.nautica_ies_backend.services.UsuarioService;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Controlador REST para la gestión de {@link Usuario}.
 * <p>
 * Proporciona endpoints para listar, buscar, crear, actualizar y eliminar
 * usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    /**
     * Constructor que inyecta el servicio de usuarios.
     *
     * @param service Servicio encargado de la lógica de negocio para usuarios.
     */
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Lista los usuarios de forma paginada y ordenada.
     * <p>
     * Endpoint: {@code GET /api/usuarios?page=0&size=10&sort=apellido,asc}
     *
     * @param page Número de página (por defecto 0).
     * @param size Tamaño de la página (por defecto 25).
     * @param sort Campo y dirección de orden (por defecto "idUsuario,asc").
     * @return Página de usuarios.
     */
    @GetMapping
    public ResponseEntity<Page<Usuario>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idUsuario,asc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado o excepción si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id)); // 200 o 404 (via handler)
    }

    /**
     * Busca un usuario por su correo electrónico.
     * <p>
     * Endpoint: {@code GET /api/usuarios/by-correo?correo=ejemplo@correo.com}
     *
     * @param correo Correo del usuario.
     * @return Usuario encontrado.
     */
    @GetMapping("/by-correo")
    public ResponseEntity<Usuario> porCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(service.buscarPorCorreo(correo));
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param usuario    Datos del usuario a crear (validados).
     * @param uriBuilder Constructor para la cabecera Location.
     * @return Usuario creado con status 201 y cabecera Location.
     */
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody @Valid UsuarioCreateRequest req,
            UriComponentsBuilder uriBuilder) {
        // 1) Elegir subclase concreta según el rol
        Usuario u = switch (req.rol()) {
            case "admin" -> {
                var a = new Administrador();
                // Validaciones específicas de admin
                if (req.codigoAdmin() == null || req.codigoAdmin().isBlank()) {
                    throw new IllegalArgumentException("codigo_admin es obligatorio cuando rol=admin");
                }
                a.setCodigoAdmin(req.codigoAdmin());
                a.setTipoAdmin(parseTipoAdministrador(req.tipoAdmin())); // convierte "gerente" -> GERENTE
                a.setFechaAlta(java.time.LocalDate.now());
                yield a;
            }
            case "operario" -> {
                var o = new Operario();
                if (req.legajo() == null || req.legajo().isBlank()) {
                    throw new IllegalArgumentException("legajo es obligatorio cuando rol=operario");
                }
                if (req.puesto() == null || req.puesto().isBlank()) {
                    throw new IllegalArgumentException("puesto es obligatorio cuando rol=operario");
                }
                o.setLegajo(req.legajo());
                o.setPuesto(req.puesto());
                yield o;
            }
            case "cliente" -> {
                var c = new Cliente();
                // ⚠️ CAMPOS OBLIGATORIOS DE CLIENTE
                c.setNumCliente(req.numCliente()); // <-- AHÍ VA
                c.setFechaAlta(java.time.LocalDate.now());
                c.setEstadoCliente(EstadoCliente.activo); // default sugerido
                c.setTipoCliente(TipoCliente.cliente);
                yield c;
            }

            default -> throw new IllegalArgumentException("Rol inválido: " + req.rol());
        };

        // 2) Mapear campos comunes
        u.setNombre(req.nombre());
        u.setApellido(req.apellido());
        u.setCorreo(req.correo());
        u.setContrasena(req.contrasena()); // el service encripta
        u.setDni(req.dni());
        u.setTelefono(req.telefono());
        u.setDireccion(req.direccion());
        u.setLocalidad(req.localidad());
        u.setProvincia(req.provincia());
        u.setActivo(req.activo() == null ? Boolean.TRUE : req.activo());

        // 3) Setear el enum (en minúscula, como tu enum)
        u.setRol(RolUsuario.valueOf(req.rol())); // enum en minúsculas: admin/operario/cliente

        // 4) Guardar
        Usuario creado = service.crear(u);

        var location = uriBuilder.path("/api/usuarios/{id}")
                .buildAndExpand(creado.getIdUsuario())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    private TipoAdministrador parseTipoAdministrador(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("tipo_admin es obligatorio cuando rol=admin");
        }
        return TipoAdministrador.valueOf(raw.trim()); // "gerente" -> GERENTE
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id      ID del usuario a actualizar.
     * @param usuario Datos actualizados (validados).
     * @return Usuario actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody @Valid Usuario usuario) {
        return ResponseEntity.ok(service.actualizar(id, usuario)); // 200 OK
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Respuesta sin contenido (204).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build(); // 204
    }


    /** GET /api/usuarios/ids-por-correo?correo=cliente@nautica.com */
    @GetMapping("/ids-por-correo")
    public ResponseEntity<Map<String, Object>> idsPorCorreo(@RequestParam String correo) {
        return ResponseEntity.ok(service.idsPorCorreo(correo));
    }
}
