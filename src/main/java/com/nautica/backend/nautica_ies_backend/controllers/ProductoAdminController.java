// src/main/java/com/nautica/backend/nautica_ies_backend/controllers/ProductoAdminController.java
package com.nautica.backend.nautica_ies_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoCreateUpdateRequest;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ActualizarStockRequest;
import com.nautica.backend.nautica_ies_backend.services.ProductoService;

@RestController
@RequestMapping("/api/admin/productos")
@CrossOrigin(origins = "*")
public class ProductoAdminController {

    private final ProductoService productoService;

    public ProductoAdminController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET /api/admin/productos
    @GetMapping
    public List<ProductoAdminDTO> listar() {
        return productoService.listarProductosAdmin();
    }

    // GET /api/admin/productos/{id}
    @GetMapping("/{id}")
    public ProductoAdminDTO obtener(@PathVariable Long id) {
        return productoService.obtenerProductoAdmin(id);
    }

    // POST /api/admin/productos
    @PostMapping
    public ResponseEntity<ProductoAdminDTO> crear(@RequestBody ProductoCreateUpdateRequest req) {
        var dto = productoService.crearProducto(req);
        return ResponseEntity.ok(dto);
    }

    // PUT /api/admin/productos/{id}
    @PutMapping("/{id}")
    public ProductoAdminDTO actualizar(
            @PathVariable Long id,
            @RequestBody ProductoCreateUpdateRequest req
    ) {
        return productoService.actualizarProducto(id, req);
    }

    // PUT /api/admin/productos/{id}/stock
    @PutMapping("/{id}/stock")
    public ProductoAdminDTO actualizarStock(
            @PathVariable Long id,
            @RequestBody ActualizarStockRequest req
    ) {
        return productoService.actualizarStock(id, req.stock());
    }

    // DELETE /api/admin/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
