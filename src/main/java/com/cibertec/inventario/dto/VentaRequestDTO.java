package com.cibertec.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VentaRequestDTO (
        @NotNull(message = "El ID del producto es obligatorio")
        UUID productoId,

        @NotNull(message = "La cantidad a vender es obligatoria")
        @Min(value = 1, message = "La cantidad minima a vender es 1")
        Integer cantidad
){}
