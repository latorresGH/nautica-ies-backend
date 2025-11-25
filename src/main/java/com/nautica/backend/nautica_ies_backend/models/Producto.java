package com.nautica.backend.nautica_ies_backend.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "numero_articulo", nullable = false)
    private Integer numeroArticulo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_almacenamiento", length = 50)
    private String codigoAlmacenamiento;

    @Column(name = "estado", length = 20)
    private String estado; // 'disponible', etc.

    @Column(name = "stock")
    private Integer stock;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<PedidoProducto> pedidosProductos = new ArrayList<>();

    // ====== getters y setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroArticulo() {
        return numeroArticulo;
    }

    public void setNumeroArticulo(Integer numeroArticulo) {
        this.numeroArticulo = numeroArticulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoAlmacenamiento() {
        return codigoAlmacenamiento;
    }

    public void setCodigoAlmacenamiento(String codigoAlmacenamiento) {
        this.codigoAlmacenamiento = codigoAlmacenamiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<PedidoProducto> getPedidosProductos() {
        return pedidosProductos;
    }

    public void setPedidosProductos(List<PedidoProducto> pedidosProductos) {
        this.pedidosProductos = pedidosProductos;
    }
}

