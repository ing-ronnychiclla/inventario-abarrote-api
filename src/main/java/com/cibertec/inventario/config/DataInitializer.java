package com.cibertec.inventario.config;

import com.cibertec.inventario.entity.Usuario;
import com.cibertec.inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            usuarioRepository.save(admin);
            System.out.println("=== Usuario admin creado automáticamente ===");
        }

        if (usuarioRepository.findByUsername("encargado").isEmpty()) {
            Usuario encargado = new Usuario();
            encargado.setUsername("encargado");
            encargado.setPassword(passwordEncoder.encode("encargado123"));
            encargado.setRol("ENCARGADO");
            usuarioRepository.save(encargado);
            System.out.println("=== Usuario encargado creado automáticamente ===");
        }

        if (usuarioRepository.findByUsername("vendedor").isEmpty()) {
            Usuario vendedor = new Usuario();
            vendedor.setUsername("vendedor");
            vendedor.setPassword(passwordEncoder.encode("vendedor123"));
            vendedor.setRol("VENDEDOR");
            usuarioRepository.save(vendedor);
            System.out.println("=== Usuario vendedor creado automáticamente ===");
        }
    }
}