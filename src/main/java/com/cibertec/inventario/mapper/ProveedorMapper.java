package com.cibertec.inventario.mapper;

import com.cibertec.inventario.dto.ProveedorRequestDTO;
import com.cibertec.inventario.dto.ProveedorResponseDTO;
import com.cibertec.inventario.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequestDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(dto.razonSocial());
        proveedor.setContacto(dto.contacto());
        proveedor.setTelefono(dto.telefono());
        return proveedor;
    }

    public ProveedorResponseDTO toResponse(Proveedor entity) {
        return new ProveedorResponseDTO(
                entity.getId(),
                entity.getRazonSocial(),
                entity.getContacto(),
                entity.getTelefono()
        );
    }
}
