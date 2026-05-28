package com.cibertec.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IngresoMercanciaRequestDTO(

        @NotNull(message = "El ID del producto es obligatorio")
        UUID productoId,

        @NotNull(message = "El ID del provvedor es obligatorio")
        UUID proveedorId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "Debe ingresar al menos 1 unidad")
        Integer cantidad,

        @NotNull(message = "El costo unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor a cero")
        BigDecimal costoUnitario,

        // @Future asegura que la fecha enviada sea mayor al dia de hoy
        @NotNull(message = "La fecha de vencimiento es obligatoria")
        @Future(message = "La fecha de vencimiento debe ser una fecha futura")
        LocalDate fechaVencimiento

) {
}
