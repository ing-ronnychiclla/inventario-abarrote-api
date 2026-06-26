package com.cibertec.inventario.dto;

import com.cibertec.inventario.entity.Kardex.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record KardexResponseDTO(
        UUID id,
        UUID productoId,
        String productoNombre,
        UUID loteId,
        TipoMovimiento tipoMovimiento,
        Integer cantidad,
        BigDecimal costoUnitario,
        String motivo,
        LocalDate fechaVencimiento,
        LocalDateTime fechaMovimiento
) {
}
