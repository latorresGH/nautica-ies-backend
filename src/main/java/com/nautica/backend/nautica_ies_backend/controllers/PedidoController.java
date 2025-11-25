package com.nautica.backend.nautica_ies_backend.controllers;

import com.nautica.backend.nautica_ies_backend.controllers.dto.Pedido.CrearPedidoRequest;
import com.nautica.backend.nautica_ies_backend.models.Pedido;
import com.nautica.backend.nautica_ies_backend.services.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // ajustá si tenés CORS configurado de otra forma
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody CrearPedidoRequest request) {
        Pedido pedido = pedidoService.crearPedido(request);

        // Podés armar un DTO de respuesta, por ahora devuelvo algo simple
        Map<String, Object> body = Map.of(
                "idPedido", pedido.getId(),
                "numeroPedido", pedido.getNumeroPedido(),
                "total", pedido.getPrecioTotal(),
                "estado", pedido.getEstado()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
