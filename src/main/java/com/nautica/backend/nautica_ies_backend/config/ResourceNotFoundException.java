// src/main/java/com/nautica/backend/nautica_ies_backend/config/ResourceNotFoundException.java
package com.nautica.backend.nautica_ies_backend.config;

/**
 * Excepción personalizada que indica que un recurso solicitado no fue encontrado.
 * <p>
 * Se lanza comúnmente desde servicios o controladores cuando no se encuentra un
 * recurso con el ID o criterio especificado.
 * <p>
 * Esta excepción puede ser capturada globalmente mediante un {@code @ControllerAdvice}
 * para devolver una respuesta HTTP 404 al cliente.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructor que recibe un mensaje descriptivo.
     *
     * @param msg Mensaje que describe el error.
     */
    public ResourceNotFoundException(String msg) { super(msg); }
}
