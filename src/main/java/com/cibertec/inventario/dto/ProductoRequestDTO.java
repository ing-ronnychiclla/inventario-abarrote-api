package com.cibertec.inventario.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoRequestDTO(
        @NotBlank(message = "El codigo de barras es obligatorio")
        String codigoBarras,

        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        BigDecimal precioVenta,

        @Min(value = 1, message = "El stock minimo no puede ser menor a 1")
        Integer stockMinimo,

        @NotNull(message = "Debe especificar la categoria del producto")
        UUID categoriaId
) {
}
