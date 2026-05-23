package com.cibertec.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
        @NotBlank(message = "El nombre de la categoria es obligatorio")
        String nombre
) {
}
