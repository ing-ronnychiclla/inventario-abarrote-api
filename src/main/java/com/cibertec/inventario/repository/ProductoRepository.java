package com.cibertec.inventario.repository;

import com.cibertec.inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    // Método crucial para cuando el cajero pase el lector de código de barras
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    // Para mostrar un catalogo filtrado en el frontend
    List<Producto> findByCodigoBarras(UUID categoriaId);

    // Buscar productos por nombre (buscador general)
    // El "ContainingIgnoreCase" hace que funcione como un "LIKE %nombre%" insensible a mayúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
