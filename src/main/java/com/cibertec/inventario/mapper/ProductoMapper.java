package com.cibertec.inventario.mapper;

import com.cibertec.inventario.dto.ProductoRequestDTO;
import com.cibertec.inventario.dto.ProductoResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import com.cibertec.inventario.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    // Convertimos el Request a Entidad, inyectando la Categoria real
    public Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        Producto producto = new Producto();
        producto.setCodigoBarras(dto.codigoBarras());
        producto.setNombre(dto.nombre());
        producto.setPrecioVenta(dto.precioVenta());
        producto.setStockMinimo(dto.stockMinimo());
        // Asignamos la relacion JPA
        producto.setCategoria(categoria);

        return producto;
    }

    // Convertimos la Entidad a Response, extrayendo datos planos para el frontend
    public ProductoResponseDTO toResponse(Producto entity) {
        return new ProductoResponseDTO(
                entity.getId(),
                entity.getCodigoBarras(),
                entity.getNombre(),
                entity.getPrecioVenta(),
                entity.getStockMinimo(),
                entity.getCategoria().getId(),
                entity.getCategoria().getNombre()
        );
    }
}
