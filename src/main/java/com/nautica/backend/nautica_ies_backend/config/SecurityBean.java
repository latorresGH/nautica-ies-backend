package com.nautica.backend.nautica_ies_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBean {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 10 es fuerza por defecto; podés subirla (12) si querés más costoso
        return new BCryptPasswordEncoder();
    }
}
