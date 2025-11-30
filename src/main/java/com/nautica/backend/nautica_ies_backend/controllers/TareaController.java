// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/TareaController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.BarSemana;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.ConfirmarTareaRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.ResumenTareasOperarioDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.TareaOperarioDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Tareas.TareasDelDia;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.Operario;
import com.nautica.backend.nautica_ies_backend.models.Tarea;
import com.nautica.backend.nautica_ies_backend.models.Turno;
import com.nautica.backend.nautica_ies_backend.models.enums.EstadoTarea;
import com.nautica.backend.nautica_ies_backend.models.enums.TipoTarea;
import com.nautica.backend.nautica_ies_backend.repository.TareaRepository;
import com.nautica.backend.nautica_ies_backend.services.TareaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;
    private final TareaRepository repo;

    public TareaController(TareaService service, TareaRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    // @GetMapping
    // public ResponseEntity<Page<Tarea>> listar(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "25") int size,
    //         @RequestParam(defaultValue = "idTarea,asc") String sort) {
    //     String[] s = sort.split(",");
    //     Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    //     Sort sortObj = Sort.by(dir, s[0]);
    //     return ResponseEntity.ok(service.listar(page, size, sortObj));
    // }
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/by-numero")
    public ResponseEntity<Tarea> porNumero(@RequestParam("valor") Integer numero) {
        return ResponseEntity.ok(service.buscarPorNumero(numero));
    }

    @GetMapping("/by-operario")
    public ResponseEntity<List<Tarea>> porOperario(@RequestParam("idOperario") Long idOperario) {
        return ResponseEntity.ok(service.listarPorOperario(idOperario));
    }

    @PostMapping
    public ResponseEntity<Tarea> crear(@RequestBody @Valid Tarea tarea, UriComponentsBuilder uriBuilder) {
        Tarea creada = service.crear(tarea);
        var location = uriBuilder.path("/api/tareas/{id}")
                .buildAndExpand(creada.getIdTarea())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(@PathVariable Long id, @RequestBody @Valid Tarea tarea) {
        return ResponseEntity.ok(service.actualizar(id, tarea));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Tarea>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "idTarea,asc") String sort) {

        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && s[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        return ResponseEntity.ok(service.listar(page, size, sortObj));
    }

    // 🔹 NUEVO: /api/tareas?fecha=YYYY-MM-DD  (Tareas del día)
    @GetMapping(params = "fecha")
    public ResponseEntity<List<TareasDelDia>> listarPorFecha(
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<Tarea> tareas = service.listarPorFecha(fecha);

        List<TareasDelDia> dto = tareas.stream()
                .map(this::toTareasDelDia)
                .toList();

        return ResponseEntity.ok(dto);
    }

    private TareasDelDia toTareasDelDia(Tarea t) {
        Operario op = t.getOperario();
        Turno turno = t.getTurno();

        Cliente cliente = null;
        Embarcacion emb = null;

        if (turno != null) {
            cliente = turno.getCliente();       // ajustá el nombre del método si es diferente
            emb = turno.getEmbarcacion();   // idem
        }

        String nombreCliente = cliente != null && cliente.getNombre() != null
                ? cliente.getNombre()
                : "";

        String apellidoCliente = cliente != null && cliente.getApellido() != null
                ? cliente.getApellido()
                : "";

        String telefonoCliente = cliente != null && cliente.getTelefono() != null
                ? cliente.getTelefono()
                : "";

        String embarcacion = "";
        if (emb != null) {
            // 👇 AJUSTÁ ESTO al nombre real del campo en tu entidad Embarcacion
            embarcacion = emb.getNombre() != null ? emb.getNombre() : "";
            // si en tu modelo es getNombreEmbarcacion(), usá eso en vez de getNombre()
        }

        String tarea = t.getTipoTarea() != null
                ? t.getTipoTarea().name()
                : "";

        String operario = "";
        if (op != null) {
            String n = op.getNombre() != null ? op.getNombre() : "";
            String a = op.getApellido() != null ? op.getApellido() : "";
            operario = (n + " " + a).trim();
        }

        String horario = t.getHora() != null
                ? t.getHora().toString() // después si querés, mandás "HH:mm" con un formatter
                : "";

        return new TareasDelDia(
                t.getIdTarea(),
                nombreCliente,
                apellidoCliente,
                embarcacion,
                telefonoCliente,
                tarea,
                operario,
                horario
        );
    }
    // 🔹 NUEVO: /api/tareas/semana?offset=1

    @GetMapping("/semana")
    public ResponseEntity<BarSemana> semana(
            @RequestParam(name = "offset", defaultValue = "0") int offset) {

        BarSemana dto = service.resumenSemana(offset);
        return ResponseEntity.ok(dto);
    }

    private TareaOperarioDTO toTareaOperarioDTO(Tarea t) {
        Cliente cliente = null;
        Embarcacion emb = null;

        if (t.getTurno() != null) {
            cliente = t.getTurno().getCliente();
            emb = t.getTurno().getEmbarcacion();
        }

        String nombre = (cliente != null && cliente.getNombre() != null)
                ? cliente.getNombre()
                : "";
        String apellido = (cliente != null && cliente.getApellido() != null)
                ? cliente.getApellido()
                : "";
        String telefono = (cliente != null && cliente.getTelefono() != null)
                ? cliente.getTelefono()
                : "";

        String embarcacion = "";
        if (emb != null && emb.getNombre() != null) {
            // 👈 si en tu entidad se llama distinto (ej: getNombreEmbarcacion),
            // cambialo acá
            embarcacion = emb.getNombre();
        }

        // ⛵ NUEVO: matricula y marca de la embarcación
        String matricula = "";
        String marca = "";
        if (emb != null) {
            // ajustá estos getters a los nombres reales de tu entidad Embarcacion
            if (emb.getNumMatricula() != null) {
                matricula = emb.getNumMatricula();
            }
            if (emb.getMarcaCasco() != null) {
                marca = emb.getMarcaCasco();
            }
        }

        String tipo = (t.getTipoTarea() != null) ? t.getTipoTarea().name() : "";
        String estado = (t.getEstado() != null) ? t.getEstado().name() : "";

        String fecha = (t.getFecha() != null) ? t.getFecha().toString() : "";
        String hora = (t.getHora() != null) ? t.getHora().toString() : "";

        return new TareaOperarioDTO(
                t.getIdTarea(),
                tipo,
                estado,
                nombre,
                apellido,
                embarcacion,
                telefono,
                fecha,
                hora,
                t.getNotaOperario(),
                marca,
                matricula
                
        );

    }

    // 🔹 Pendientes por tipo (para la pestaña "Pendientes")
    @GetMapping("/operario/lista")
    public ResponseEntity<List<TareaOperarioDTO>> listarOperario(
            @RequestParam("tipo") TipoTarea tipo,
            @RequestParam("estado") EstadoTarea estado,
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        List<Tarea> tareas;

        if (desde != null && hasta != null) {
            tareas = repo.findByTipoTareaAndEstadoAndFechaBetweenOrderByFechaAscHoraAsc(
                    tipo, estado, desde, hasta
            );
        } else {
            // si no mandan fechas, devolvemos todas (o podrías limitar a hoy, según lo que quieras)
            tareas = repo.findByTipoTareaAndEstadoOrderByFechaAscHoraAsc(tipo, estado);
        }

        List<TareaOperarioDTO> dto = tareas.stream()
                .map(this::toTareaOperarioDTO)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<TareaOperarioDTO> confirmarTarea(
            @PathVariable Long id,
            @RequestBody ConfirmarTareaRequest body
    ) {
        Tarea t = service.confirmarTarea(id, body.notaOperario());
        return ResponseEntity.ok(toTareaOperarioDTO(t));
    }

    @GetMapping("/operario/resumen-hoy")
    public ResponseEntity<ResumenTareasOperarioDTO> resumenHoyOperario() {
        return ResponseEntity.ok(service.resumenPendientesHoy());
    }

}
