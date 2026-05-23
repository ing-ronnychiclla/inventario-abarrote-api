package com.cibertec.inventario.repository;

import com.cibertec.inventario.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    // Util para validar que no intentemos crear dos categorías con el mismo nombre
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
