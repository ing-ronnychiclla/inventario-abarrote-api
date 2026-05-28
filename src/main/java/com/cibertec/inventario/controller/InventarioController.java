package com.cibertec.inventario.controller;

import com.cibertec.inventario.dto.IngresoMercanciaRequestDTO;
import com.cibertec.inventario.dto.IngresoMercanciaResponseDTO;
import com.cibertec.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
