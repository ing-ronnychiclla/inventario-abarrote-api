package com.cibertec.inventario.mapper;

import com.cibertec.inventario.dto.CategoriaRequestDTO;
import com.cibertec.inventario.dto.CategoriaResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    // De DTO a Entidad
    // Recibiré un DTO y devolveré una entidad Categoria
    public Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre()); // en records el getter es el mismo nombre de la variable
        return categoria;
    }

    // De Entidad a DTO
    // Recibiré una entidad y devolveré un DTO de respuesta.
    public CategoriaResponseDTO toResponse(Categoria entity) {
        return new CategoriaResponseDTO(
                entity.getId(),
                entity.getNombre()
        );
    }
}
