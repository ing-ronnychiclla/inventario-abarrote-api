package com.cibertec.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kardex")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kardex {

    public enum TipoMovimiento {
        ENTRADA, SALIDA_VENTA, SALIDA_MERMA, AJUSTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote; // Puede ser nulo si es un ajuste general sin lote especifico

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad; // Siempre en positivo. El enum indica si suma o resta

    @Column(length = 255)
    private String motivo;

    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento =  LocalDateTime.now();
}
