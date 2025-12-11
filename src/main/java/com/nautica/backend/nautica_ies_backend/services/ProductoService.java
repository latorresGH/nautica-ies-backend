// src/main/java/com/nautica/backend/nautica_ies_backend/services/ProductoService.java
package com.nautica.backend.nautica_ies_backend.services;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoAdminDTO;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoTienda;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Producto.ProductoCreateUpdateRequest;
import com.nautica.backend.nautica_ies_backend.models.Producto;
import com.nautica.backend.nautica_ies_backend.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    
    @Value("${app.upload-dir}")
    private String uploadDir;

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
                p.getStock(),
                p.getImagenUrl()
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

 public void eliminarProducto(Long id) {
    if (!productoRepository.existsById(id)) {
        throw new ResourceNotFoundException("Producto no encontrado con id " + id);
    }
    productoRepository.deleteById(id);
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
                p.getStock(),
                p.getImagenUrl()
        );
    }


     public ProductoAdminDTO guardarImagen(Long idProducto, MultipartFile file) throws IOException {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + idProducto));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen está vacío.");
        }

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Nombre de archivo nulo");
        String extension = "";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String nombreArchivo = UUID.randomUUID() + extension;

        // Carpeta física: uploads/productos/
        Path carpeta = Paths.get(uploadDir, "productos").toAbsolutePath().normalize();
        Files.createDirectories(carpeta);

        Path destino = carpeta.resolve(nombreArchivo);
        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        // URL pública (como la ve el front)
        String urlPublica = "/images/productos/" + nombreArchivo;
        producto.setImagenUrl(urlPublica);

        Producto guardado = productoRepository.save(producto);
        return toAdminDTO(guardado);
    }
}

