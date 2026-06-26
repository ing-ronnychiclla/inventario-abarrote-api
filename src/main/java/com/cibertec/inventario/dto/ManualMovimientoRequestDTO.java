package com.cibertec.inventario.dto;

import com.cibertec.inventario.entity.Kardex.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ManualMovimientoRequestDTO(
        @NotNull UUID productoId,
        @NotNull TipoMovimiento tipoMovimiento,
        @NotNull @Min(1) Integer cantidad,
        String motivo
) {
}
