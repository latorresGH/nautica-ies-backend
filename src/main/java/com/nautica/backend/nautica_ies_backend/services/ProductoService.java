// src/main/java/com/nautica/backend/nautica_ies_backend/services/ProductoService.java
package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoTienda;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoCreateUpdateRequest;
import com.nautica.backend.nautica_ies_backend.models.Producto;
import com.nautica.backend.nautica_ies_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoTienda> listarProductosTienda() {
        // podés filtrar solo estado = 'disponible' si querés
        return productoRepository.findAll()
                .stream()
                .map(this::toTiendaDTO)
                .toList();
    }

    private ProductoTienda toTiendaDTO(Producto p) {
        return new ProductoTienda(
                p.getId(),
                p.getNumeroArticulo(),
                p.getNombre(),
                p.getPrecioUnitario(),
                p.getCategoria(),
                p.getDescripcion(),
                p.getStock()
        );
    }

    /* ================== PARA ADMIN ================== */

    public List<ProductoAdminDTO> listarProductosAdmin() {
        return productoRepository.findAll()
                .stream()
                .map(this::toAdminDTO)
                .toList();
    }

    public ProductoAdminDTO obtenerProductoAdmin(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
        return toAdminDTO(p);
    }

    public ProductoAdminDTO crearProducto(ProductoCreateUpdateRequest req) {
        Producto p = new Producto();
        aplicarDatos(p, req);
        Producto guardado = productoRepository.save(p);
        return toAdminDTO(guardado);
    }

    public ProductoAdminDTO actualizarProducto(Long id, ProductoCreateUpdateRequest req) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
        aplicarDatos(p, req);
        Producto guardado = productoRepository.save(p);
        return toAdminDTO(guardado);
    }

    public ProductoAdminDTO actualizarStock(Long id, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
        p.setStock(nuevoStock);
        Producto guardado = productoRepository.save(p);
        return toAdminDTO(guardado);
    }

    /** "Eliminar": por ahora lo hacemos lógico cambiando estado */
    public void eliminarProducto(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));

        // si querés eliminar físico:
        // productoRepository.delete(p);

        // versión soft-delete:
        p.setEstado("no_disponible");
        productoRepository.save(p);
    }

    /* ================== MAPPERS INTERNOS ================== */

    private void aplicarDatos(Producto p, ProductoCreateUpdateRequest req) {
        p.setNumeroArticulo(req.numeroArticulo());
        p.setNombre(req.nombre());
        p.setPrecioUnitario(req.precioUnitario());
        p.setCategoria(req.categoria());
        p.setDescripcion(req.descripcion());
        p.setCodigoAlmacenamiento(req.codigoAlmacenamiento());
        p.setEstado(req.estado());
        p.setStock(req.stock());
    }

    private ProductoAdminDTO toAdminDTO(Producto p) {
        return new ProductoAdminDTO(
                p.getId(),
                p.getNumeroArticulo(),
                p.getNombre(),
                p.getPrecioUnitario(),
                p.getCategoria(),
                p.getDescripcion(),
                p.getCodigoAlmacenamiento(),
                p.getEstado(),
                p.getStock()
        );
    }
}
