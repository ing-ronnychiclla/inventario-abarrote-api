package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.ProductoRequestDTO;
import com.cibertec.inventario.dto.ProductoResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import com.cibertec.inventario.entity.Producto;
import com.cibertec.inventario.mapper.ProductoMapper;
import com.cibertec.inventario.repository.CategoriaRepository;
import com.cibertec.inventario.repository.LoteRepository;
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
    private final LoteRepository loteRepository;

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

        // 5. Retornar el Response DTO (stock inicial es 0 para un producto nuevo)
        return productoMapper.toResponse(productoGuardado, 0);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        List<Producto> productos = productoRepository.findAll();
        
        // Obtener stock real disponible agrupado por producto para evitar consultas N+1
        List<Object[]> stocksRaw = loteRepository.obtenerStockDisponiblePorProducto();
        java.util.Map<UUID, Integer> stockMap = stocksRaw.stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> ((Number) row[1]).intValue(),
                        (a, b) -> a
                ));

        return productos.stream()
                .map(p -> {
                    Integer stockReal = stockMap.getOrDefault(p.getId(), 0);
                    return productoMapper.toResponse(p, stockReal);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Integer stockReal = loteRepository.sumCantidadActualByProductoId(id);
        return productoMapper.toResponse(producto, stockReal);
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(UUID id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
        producto.setNombre(dto.nombre());
        producto.setCodigoBarras(dto.codigoBarras());
        producto.setPrecioVenta(dto.precioVenta());
        producto.setStockMinimo(dto.stockMinimo());
        producto.setCategoria(categoria);
        
        Producto productoGuardado = productoRepository.save(producto);
        Integer stockReal = loteRepository.sumCantidadActualByProductoId(id);
        return productoMapper.toResponse(productoGuardado, stockReal);
    }

    @Transactional
    public void eliminarProducto(UUID id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }
}
