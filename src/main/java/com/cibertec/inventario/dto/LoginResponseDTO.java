package com.cibertec.inventario.dto;

public record LoginResponseDTO(
        String mensaje,
        String username,
        String rol,
        String token
) {}