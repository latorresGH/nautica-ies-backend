package com.nautica.backend.nautica_ies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nautica.backend.nautica_ies_backend.models.Embarcacion;
import com.nautica.backend.nautica_ies_backend.models.UsuarioEmbarcacion;
import com.nautica.backend.nautica_ies_backend.models.enums.RolEnEmbarcacion;

public interface UsuarioEmbarcacionRepository extends JpaRepository<UsuarioEmbarcacion, Long> {
    List<UsuarioEmbarcacion> findByEmbarcacion_IdEmbarcacion(Long idEmbarcacion);
    
    java.util.List<UsuarioEmbarcacion> findByUsuario_IdUsuario(Long idUsuario);

    Optional<UsuarioEmbarcacion> findByUsuario_IdUsuarioAndEmbarcacion_IdEmbarcacion(Long idUsuario,
            Long idEmbarcacion);

    // ✅ Nuevo: traer relaciones activas (hasta null o futura)
    @Query("""
        select ue from UsuarioEmbarcacion ue
        where ue.usuario.idUsuario = :usuarioId
          and (ue.hasta is null or ue.hasta >= current_date)
    """)
    List<UsuarioEmbarcacion> findActivasByUsuario(Long usuarioId);

    @org.springframework.data.jpa.repository.Modifying
    void deleteByUsuario_IdUsuario(Long idUsuario);

    long countByEmbarcacion_IdEmbarcacion(Long idEmbarcacion);

    List<UsuarioEmbarcacion> findByRolEnEmbarcacionAndHastaIsNull(RolEnEmbarcacion rolEnEmbarcacion);

    //metodo para buscar autorizados
    List<UsuarioEmbarcacion> findByEmbarcacionInAndRolEnEmbarcacion(
        List<Embarcacion> embarcaciones,
        RolEnEmbarcacion rolEnEmbarcacion
);

}
