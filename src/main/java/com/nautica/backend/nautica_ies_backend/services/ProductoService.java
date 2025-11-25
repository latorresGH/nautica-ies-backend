// src/main/java/com/nautica/backend/nautica_ies_backend/services/ProductoService.java
package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoTienda;
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
}
