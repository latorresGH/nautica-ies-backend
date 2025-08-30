package com.nautica.backend.nautica_ies_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del proyecto <strong>Náutica IES Backend</strong>.
 * <p>
 * Es el punto de entrada de la aplicación Spring Boot.
 * Esta clase lanza el contexto de Spring y ejecuta la aplicación.
 */
@SpringBootApplication
public class NauticaIesBackendApplication {

     /**
      * Método principal que inicia la aplicación Spring Boot.
      *
      * @param args Argumentos de línea de comandos.
      */
     public static void main(String[] args) {
          SpringApplication.run(NauticaIesBackendApplication.class, args);
     }

}
