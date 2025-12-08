//D:\FACULTAD\Proyecto Final\nautica-ies-backend\src\main\java\com\nautica\backend\nautica_ies_backend\controllers\dto\EmbarcacionResumenDTO.java
package com.nautica.backend.nautica_ies_backend.controllers.dto;

import java.time.LocalDate;

public record EmbarcacionResumenDTO(
    Long id,
    String nombre,
    String matricula,
    String marcaCasco,
    String modeloCasco,
    String marcaMotor,
    String modeloMotor,
    Integer potenciaMotor,
    LocalDate fechaAlta
) {}
