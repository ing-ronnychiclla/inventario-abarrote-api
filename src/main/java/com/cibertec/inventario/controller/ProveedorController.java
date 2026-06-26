package com.cibertec.inventario.controller;

import com.cibertec.inventario.dto.CategoriaRequestDTO;
import com.cibertec.inventario.dto.CategoriaResponseDTO;
import com.cibertec.inventario.dto.ProveedorRequestDTO;
import com.cibertec.inventario.dto.ProveedorResponseDTO;
import com.cibertec.inventario.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/proveedor")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crearProveedor(@Valid @RequestBody ProveedorRequestDTO request) {
        ProveedorResponseDTO response = proveedorService.crearProveedor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> obtenerTodos() {
        List<ProveedorResponseDTO> response = proveedorService.obtenerTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerPorId(@PathVariable UUID id) {
        ProveedorResponseDTO response = proveedorService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(
            @PathVariable UUID id,
            @Valid @RequestBody ProveedorRequestDTO request) {
        ProveedorResponseDTO response = proveedorService.actualizarProveedor(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable UUID id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
