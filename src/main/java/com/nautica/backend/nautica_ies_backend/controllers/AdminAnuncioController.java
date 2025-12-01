package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioAdminRequestDTO;
import com.nautica.backend.nautica_ies_backend.services.AnuncioService;

@RestController
@RequestMapping("/api/admin/anuncios")
public class AdminAnuncioController {

    private final AnuncioService anuncioService;

    public AdminAnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping
    public List<AnuncioAdminDTO> listarTodos() {
        return anuncioService.listarTodosAdmin();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnuncioAdminDTO crear(@RequestBody AnuncioAdminRequestDTO dto) {
        return anuncioService.crearAnuncio(dto);
    }

    @PutMapping("/{id}")
    public AnuncioAdminDTO actualizar(@PathVariable Long id, @RequestBody AnuncioAdminRequestDTO dto) {
        return anuncioService.actualizarAnuncio(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        anuncioService.eliminarAnuncio(id);
    }
}
