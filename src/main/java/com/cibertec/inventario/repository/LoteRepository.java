package com.cibertec.inventario.repository;

import com.cibertec.inventario.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoteRepository extends JpaRepository<Lote, UUID> {

    // Busca lotes de un producto que aun tengan existencias fisicas
    // y los ordena poniendo de primeros aquellos que caducan mas pronto
    @Query("SELECT l FROM Lote l WHERE l.producto.id = :productoId AND l.cantidadActual > 0 ORDER BY l.fechaVencimiento ASC")
    List<Lote> findLotesConStockPorProducto(@Param("productoId") UUID productoId);
}
