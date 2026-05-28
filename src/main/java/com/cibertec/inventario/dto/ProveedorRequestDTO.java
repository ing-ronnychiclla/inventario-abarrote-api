package com.cibertec.inventario.dto;


import jakarta.validation.constraints.NotBlank;

public record ProveedorRequestDTO(

        @NotBlank(message = "La razon social es obligatorio")
        String razonSocial,

        @NotBlank(message = "El contacto es obligatorio")
        String contacto,

        @NotBlank(message = "El numero de telefono es obligatorio")
        String telefono
) {
}
