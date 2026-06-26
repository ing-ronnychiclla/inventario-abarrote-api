package com.cibertec.inventario.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        String mensaje,
        List<String> detalles, // Util para enviar multiples errores a la vez (ej. validaciones de formulario)
        LocalDateTime timestamp
) {
}
