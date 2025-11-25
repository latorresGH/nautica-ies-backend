// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/ProductoController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoTienda;
import com.nautica.backend.nautica_ies_backend.services.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // ajustá si ya tenés CORS global
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    public List<ProductoTienda> listarProductos() {
        return productoService.listarProductosTienda();
    }
}
