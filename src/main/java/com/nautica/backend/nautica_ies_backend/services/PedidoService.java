package com.nautica.backend.nautica_ies_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nautica.backend.nautica_ies_backend.config.ResourceNotFoundException;
import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.CrearPedidoRequest;
import com.nautica.backend.nautica_ies_backend.models.Cliente;
import com.nautica.backend.nautica_ies_backend.models.Pedido;
import com.nautica.backend.nautica_ies_backend.models.PedidoProducto;
import com.nautica.backend.nautica_ies_backend.models.Producto;
import com.nautica.backend.nautica_ies_backend.repository.ClienteRepository;
import com.nautica.backend.nautica_ies_backend.repository.PedidoRepository;
import com.nautica.backend.nautica_ies_backend.repository.ProductoRepository;


@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    public Pedido crearPedido(CrearPedidoRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto.");
        }

        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + request.idCliente()));

        // Obtener ids de productos únicos del request
        List<Long> idsProductos = request.items().stream()
                .map(CrearPedidoRequest.ItemPedidoRequest::idProducto)
                .distinct()
                .toList();

        // Traer productos de la BD
        Map<Long, Producto> productosMap = productoRepository.findAllById(idsProductos)
                .stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        // Validar productos y stock, calcular total
        BigDecimal total = BigDecimal.ZERO;
        List<PedidoProducto> items = new ArrayList<>();

        for (CrearPedidoRequest.ItemPedidoRequest itemReq : request.items()) {
            Producto producto = productosMap.get(itemReq.idProducto());
            if (producto == null) {
                throw new ResourceNotFoundException("Producto no encontrado con id " + itemReq.idProducto());
            }

            int cantidad = Optional.ofNullable(itemReq.cantidad()).orElse(0);
            if (cantidad <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para producto " + producto.getNombre());
            }

            int stockActual = Optional.ofNullable(producto.getStock()).orElse(0);
            if (stockActual < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para producto " + producto.getNombre()
                        + ". Stock disponible: " + stockActual + ", solicitado: " + cantidad);
            }

            BigDecimal precioUnitario = producto.getPrecioUnitario();
            if (precioUnitario == null) {
                throw new IllegalStateException("Producto " + producto.getNombre() + " no tiene precio configurado.");
            }

            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            PedidoProducto pp = new PedidoProducto();
            pp.setProducto(producto);
            pp.setCantidad(cantidad);
            pp.setPrecioUnitario(precioUnitario);
            pp.setSubtotal(subtotal);
            items.add(pp);

            // Descontar stock en memoria
            producto.setStock(stockActual - cantidad);
        }

        // Crear cabecera del pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setEstado("pendiente");
        pedido.setArticulo("Compra tienda");
        pedido.setCantidad(1);
        pedido.setPrecioTotal(total);

        // Vincular items
        for (PedidoProducto pp : items) {
            pp.setPedido(pedido);
            pedido.getItems().add(pp);
        }

        // Guardar todo (cabecera + detalle) y actualiza stock de productos
        Pedido guardado = pedidoRepository.save(pedido);

        // IMPORTANTE: productoRepository se actualizará cuando el contexto de persistencia se sincronice,
        // pero si querés asegurarte:
        // productoRepository.saveAll(productosMap.values());

        return guardado;
    }

    private int generarNumeroPedido() {
        // Implementación simple (para TP):
        // podrías mejorar haciendo una consulta a la BD o usando una secuencia propia.
        long count = pedidoRepository.count() + 1;
        if (count > Integer.MAX_VALUE) {
            return (int) (count % Integer.MAX_VALUE);
        }
        return (int) count;
    }

    @Transactional
public Pedido actualizarEstadoPedido(Long idPedido, String nuevoEstadoRaw) {
    Pedido pedido = pedidoRepository.findById(idPedido)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + idPedido));

    String estadoAnterior = Optional.ofNullable(pedido.getEstado())
            .orElse("pendiente")
            .toLowerCase();

    String nuevoEstado = Optional.ofNullable(nuevoEstadoRaw)
            .orElse("pendiente")
            .toLowerCase();

    // Si no cambia nada, devolvemos tal cual
    if (estadoAnterior.equals(nuevoEstado)) {
        return pedido;
    }

    // --- REGLAS DE STOCK ---

    // 1) Pasamos de NO cancelado -> cancelado  => devolver stock
    if (!estadoAnterior.equals("cancelado") && nuevoEstado.equals("cancelado")) {
        if (pedido.getItems() != null) {
            pedido.getItems().forEach(item -> {
                Producto producto = item.getProducto();
                if (producto != null) {
                    int stockActual = Optional.ofNullable(producto.getStock()).orElse(0);
                    int cantidad = Optional.ofNullable(item.getCantidad()).orElse(0);
                    producto.setStock(stockActual + cantidad);
                    // si querés ser explícito:
                    // productoRepository.save(producto);
                }
            });
        }
    }

    // 2) (OPCIONAL) Pasamos de cancelado -> otro estado => volver a descontar stock
    // Solo si querés permitir "reactivar" pedidos
    if (estadoAnterior.equals("cancelado") && !nuevoEstado.equals("cancelado")) {
        if (pedido.getItems() != null) {
            pedido.getItems().forEach(item -> {
                Producto producto = item.getProducto();
                if (producto != null) {
                    int stockActual = Optional.ofNullable(producto.getStock()).orElse(0);
                    int cantidad = Optional.ofNullable(item.getCantidad()).orElse(0);

                    if (stockActual < cantidad) {
                        throw new IllegalStateException("No hay stock suficiente para reactivar el pedido del producto "
                                + producto.getNombre() + ". Stock: " + stockActual + ", necesario: " + cantidad);
                    }

                    producto.setStock(stockActual - cantidad);
                    // productoRepository.save(producto);
                }
            });
        }
    }

    // Finalmente, actualizamos el estado
    pedido.setEstado(nuevoEstado);

    // Como estamos en @Transactional, con esto alcanza
    return pedidoRepository.save(pedido);
}
}
