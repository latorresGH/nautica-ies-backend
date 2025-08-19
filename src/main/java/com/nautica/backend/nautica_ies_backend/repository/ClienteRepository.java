package com.nautica.backend.nautica_ies_backend.repository;

import com.nautica.backend.nautica_ies_backend.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> { }
