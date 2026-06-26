package com.cibertec.inventario.controller;

import com.cibertec.inventario.dto.*;
import com.cibertec.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping("/ingresos")
    public ResponseEntity<IngresoMercanciaResponseDTO> registrarIngreso(
            @Valid @RequestBody IngresoMercanciaRequestDTO request) {

        IngresoMercanciaResponseDTO response = inventarioService.registrarIngreso(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO response = inventarioService.registrarVenta(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<KardexResponseDTO>> obtenerKardex() {
        List<KardexResponseDTO> response = inventarioService.obtenerKardex();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ingresos")
    public ResponseEntity<List<IngresoMercanciaResponseDTO>> obtenerIngresos() {
        List<IngresoMercanciaResponseDTO> response = inventarioService.obtenerIngresos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentas() {
        List<VentaResponseDTO> response = inventarioService.obtenerVentas();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/movimientos")
    public ResponseEntity<Void> registrarMovimientoManual(@Valid @RequestBody ManualMovimientoRequestDTO request) {
        inventarioService.registrarMovimientoManual(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/ingresos/{id}")
    public ResponseEntity<IngresoMercanciaResponseDTO> actualizarIngreso(
            @PathVariable UUID id,
            @Valid @RequestBody IngresoMercanciaRequestDTO request) {
        IngresoMercanciaResponseDTO response = inventarioService.actualizarIngreso(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/ingresos/{id}")
    public ResponseEntity<Void> eliminarIngreso(@PathVariable UUID id) {
        inventarioService.eliminarIngreso(id);
        return ResponseEntity.noContent().build();
    }
}
