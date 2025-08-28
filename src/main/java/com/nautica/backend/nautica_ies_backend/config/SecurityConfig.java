package com.nautica.backend.nautica_ies_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .headers(h -> h.frameOptions(f -> f.disable())) // útil si usás H2 console en dev
      .authorizeHttpRequests(auth -> auth
          .requestMatchers("/actuator/**").permitAll()
          .requestMatchers("/api/**").permitAll()   // abrí tus APIs en dev
          .anyRequest().permitAll()
      )
      .httpBasic(b -> {}); // opcional

    return http.build();
  }
}
