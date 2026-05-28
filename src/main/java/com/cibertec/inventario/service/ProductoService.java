package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.ProductoRequestDTO;
import com.cibertec.inventario.dto.ProductoResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import com.cibertec.inventario.entity.Producto;
import com.cibertec.inventario.mapper.ProductoMapper;
import com.cibertec.inventario.repository.CategoriaRepository;
import com.cibertec.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        // 1. Validar que no exista otro producto con el mismo codigo de barras
        if (productoRepository.findByCodigoBarras(dto.codigoBarras()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el codigo de barras" + dto.codigoBarras());
        }

        // 2. Buscar la categoria en la base de datos
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("La categoria especificada no existe"));

        // 3. Mapear DTO a Entidad
        Producto producto = productoMapper.toEntity(dto, categoria);

        // 4. Guardar en la base de datos
        Producto productoGuardado = productoRepository.save(producto);

        // 5. Retornar el Response DTO
        return productoMapper.toResponse(productoGuardado);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        return productoMapper.toResponse(producto);
    }
}
