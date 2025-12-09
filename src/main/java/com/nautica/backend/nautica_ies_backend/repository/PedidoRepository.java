package com.nautica.backend.nautica_ies_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
        // List<Pedido> findAllByOrderByFechaPedidoDesc();
            Page<Pedido> findAllByOrderByFechaPedidoDesc(Pageable pageable);
    List<Pedido> findByFechaPedidoBetween(LocalDate desde, LocalDate hasta);
}
