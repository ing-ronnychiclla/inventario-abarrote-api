package com.cibertec.inventario.dto;

import java.util.UUID;

public record CategoriaResponseDTO(
        UUID id,
        String nombre
) {
}
