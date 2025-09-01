package com.nautica.backend.nautica_ies_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Administrador;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByCodigoAdmin(String codigoAdmin);
    boolean existsByCodigoAdmin(String codigoAdmin);
}
