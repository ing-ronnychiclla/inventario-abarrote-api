package com.cibertec.inventario.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResponseDTO(
        UUID id,
        String codigoBarras,
        String nombre,
        BigDecimal precioVenta,
        Integer stockMinimo,
        UUID categoriaId,
        String categoriaNombre
) {
}
