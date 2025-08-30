package com.nautica.backend.nautica_ies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;

public interface UsuarioEmbarcacionRepository extends JpaRepository<UsuarioEmbarcacion, Long> {
    List<UsuarioEmbarcacion> findByEmbarcacion_IdEmbarcacion(Long idEmbarcacion);

    List<UsuarioEmbarcacion> findByUsuario_IdUsuario(Long idUsuario);

    Optional<UsuarioEmbarcacion> findByUsuario_IdUsuarioAndEmbarcacion_IdEmbarcacion(Long idUsuario,
            Long idEmbarcacion);
}
