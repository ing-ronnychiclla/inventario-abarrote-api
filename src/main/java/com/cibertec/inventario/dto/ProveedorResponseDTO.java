package com.cibertec.inventario.dto;

import java.util.UUID;

public record ProveedorResponseDTO (
        UUID id,
        String razonSocial,
        String contacto,
        String telefono
){
}
