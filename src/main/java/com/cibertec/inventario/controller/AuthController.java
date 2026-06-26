package com.cibertec.inventario.controller;

import com.cibertec.inventario.config.JwtUtil;
import com.cibertec.inventario.dto.LoginRequestDTO;
import com.cibertec.inventario.dto.LoginResponseDTO;
import com.cibertec.inventario.entity.Usuario;
import com.cibertec.inventario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

        return ResponseEntity.ok(new LoginResponseDTO(
                "Login exitoso",
                usuario.getUsername(),
                usuario.getRol(),
                token
        ));
    }
}