package com.nautica.backend.nautica_ies_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración para definir beans relacionados con la seguridad.
 * <p>
 * Contiene el bean {@link PasswordEncoder} usado para codificar contraseñas con el algoritmo BCrypt.
 */
@Configuration
public class SecurityBean {
    /**
     * Define un bean de tipo {@link PasswordEncoder} utilizando {@link BCryptPasswordEncoder}.
     * <p>
     * BCrypt es un algoritmo de hash seguro ideal para almacenar contraseñas de forma cifrada.
     *
     * @return Instancia de {@code BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 10 es fuerza por defecto; podés subirla (12) si querés más costoso
        return new BCryptPasswordEncoder();
    }
}
