package usta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

/**
 * MODEL - Representa un equipo (portátil, cámara, sensor, etc.) que el
 * departamento de TIC presta a los estudiantes.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer stock;

    public Equipment(String code, String name, Integer stock) {
        this.code = code;
        this.name = name;
        this.stock = stock;
    }

    @Override
    // Compara por id
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    // Hash de la clase (usado por colecciones como HashSet)
    public int hashCode() {
        return getClass().hashCode();
    }
}
