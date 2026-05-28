package com.cibertec.inventario.repository;

import com.cibertec.inventario.entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, UUID> {

    // Para que el frontend pueda mostrar una tabla con el historial de que paso con un producto
    List<Kardex> findByProductoIdOrderByFechaMovimientoDesc(UUID productoId);
}
