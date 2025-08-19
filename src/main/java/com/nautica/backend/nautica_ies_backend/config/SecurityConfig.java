package com.nautica.backend.nautica_ies_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para la aplicación utilizando Spring Security con WebFlux.
 * <p>
 * Esta configuración inicial:
 * <ul>
 *     <li>Desactiva CSRF (Cross-Site Request Forgery).</li>
 *     <li>Permite acceso libre a todos los endpoints.</li>
 *     <li>Habilita autenticación básica HTTP (por defecto, sin proteger rutas).</li>
 * </ul>
 * Ideal para entorno de desarrollo o pruebas iniciales.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    /**
     * Define el filtro de seguridad para todas las peticiones HTTP del backend.
     *
     * @param http Configuración del servidor HTTP.
     * @return Filtro de seguridad aplicado a las rutas.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // deshabilito csrf y permito todo para empezar
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
