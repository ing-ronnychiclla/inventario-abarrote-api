package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.CategoriaRequestDTO;
import com.cibertec.inventario.dto.CategoriaResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import com.cibertec.inventario.mapper.CategoriaMapper;
import com.cibertec.inventario.repository.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        if (categoriaRepository.findByNombreIgnoreCase(dto.nombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoria con el nombre: "  + dto.nombre());
        }

        Categoria categoria = categoriaMapper.toEntity(dto);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        return categoriaMapper.toResponse(categoriaGuardada);
    }

    @Transactional
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaResponseDTO obtenerPorId(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

        return categoriaMapper.toResponse(categoria);
    }
}
