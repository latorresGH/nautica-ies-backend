package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Anuncio.AnuncioClienteDTO;
import com.nautica.backend.nautica_ies_backend.services.AnuncioService;

@RestController
@RequestMapping("/api/clientes/anuncios")
public class ClienteAnuncioController {

    private final AnuncioService anuncioService;

    public ClienteAnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping("/activos")
    public List<AnuncioClienteDTO> listarActivos() {
        return anuncioService.listarActivosCliente();
    }
}
