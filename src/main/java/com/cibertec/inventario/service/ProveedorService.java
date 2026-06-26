package com.cibertec.inventario.service;

import com.cibertec.inventario.dto.CategoriaRequestDTO;
import com.cibertec.inventario.dto.CategoriaResponseDTO;
import com.cibertec.inventario.dto.ProveedorRequestDTO;
import com.cibertec.inventario.dto.ProveedorResponseDTO;
import com.cibertec.inventario.entity.Categoria;
import com.cibertec.inventario.entity.Proveedor;
import com.cibertec.inventario.mapper.ProveedorMapper;
import com.cibertec.inventario.repository.ProveedorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Transactional
    public ProveedorResponseDTO crearProveedor(ProveedorRequestDTO dto) {
        if (proveedorRepository.findByRazonSocialIgnoreCase(dto.razonSocial()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoria con el nombre: "  + dto.razonSocial());
        }

        Proveedor proveedor = proveedorMapper.toEntity(dto);
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);

        return proveedorMapper.toResponse(proveedorGuardado);
    }

    @Transactional
    public List<ProveedorResponseDTO> obtenerTodos() {
        return proveedorRepository.findAll()
                .stream()
                .map(proveedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProveedorResponseDTO obtenerPorId(UUID id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        return proveedorMapper.toResponse(proveedor);
    }

    @Transactional
    public ProveedorResponseDTO actualizarProveedor(UUID id, ProveedorRequestDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        proveedor.setRazonSocial(dto.razonSocial());
        proveedor.setContacto(dto.contacto());
        proveedor.setTelefono(dto.telefono());
        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void eliminarProveedor(UUID id) {
        if (!proveedorRepository.existsById(id)) {
            throw new IllegalArgumentException("Proveedor no encontrado");
        }
        proveedorRepository.deleteById(id);
    }
}
