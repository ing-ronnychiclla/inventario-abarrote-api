package com.cibertec.inventario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // unique = true evita que existan dos categorías con el mismo nombre
    @Column(nullable = false, length = 50, unique = true)
    private String nombre;

    @Column(length = 150)
    private String descripcion;
}
