package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.models.Pedido;

import java.util.List;

import com.nautica.backend.nautica_ies_backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
        // List<Pedido> findAllByOrderByFechaPedidoDesc();
            Page<Pedido> findAllByOrderByFechaPedidoDesc(Pageable pageable);

}
