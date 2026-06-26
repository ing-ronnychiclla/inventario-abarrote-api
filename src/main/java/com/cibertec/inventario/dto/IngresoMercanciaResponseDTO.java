package com.cibertec.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record IngresoMercanciaResponseDTO(
        UUID kardexId,
        UUID loteID,
        String productoNombre,
        String proveedorRazonSocial,
        Integer cantidadIngresada,
        BigDecimal costoUnitario,
        LocalDateTime fechaRegistro,
        Integer stockInicial,
        Integer stockDisponible,
        java.time.LocalDate fechaVencimiento
) {
}
