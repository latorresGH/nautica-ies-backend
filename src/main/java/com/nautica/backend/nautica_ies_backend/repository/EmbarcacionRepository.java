package com.nautica.backend.nautica_ies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nautica.backend.nautica_ies_backend.models.Embarcacion;

/**
 * Repositorio JPA para la entidad {@link Embarcacion}.
 * <p>
 * Proporciona métodos CRUD y consultas automáticas para trabajar con embarcaciones en la base de datos.
 * <p>
 * Extiende {@link JpaRepository}, lo cual permite operaciones como:
 * <ul>
 *     <li>{@code findAll()}</li>
 *     <li>{@code findById(Long)}</li>
 *     <li>{@code save(Embarcacion)}</li>
 *     <li>{@code deleteById(Long)}</li>
 *     <li>... entre otros.</li>
 * </ul>
 */
public interface EmbarcacionRepository extends JpaRepository<Embarcacion, Long> { }
