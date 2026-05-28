package com.cibertec.inventario.repository;

import com.cibertec.inventario.entity.Proveedor;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {
    Optional<Object> findByRazonSocialIgnoreCase(String razonSocial);
}
