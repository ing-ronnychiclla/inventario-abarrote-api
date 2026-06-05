package com.cibertec.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VentaResponseDTO(
        UUID productoId,
        String productoNombre,
        Integer cantidadVendida,
        BigDecimal totalPagar,
        LocalDateTime fechaVenta
) {
}
