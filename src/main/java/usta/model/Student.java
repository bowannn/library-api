package usta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

/**
 * MODEL - Representa un estudiante que puede tomar equipos en préstamo.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String program;

    public Student(String name, String email, String program) {
        this.name = name;
        this.email = email;
        this.program = program;
    }

    @Override
    // Compara por id
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    // Hash de la clase (usado por colecciones como HashSet)
    public int hashCode() {
        return getClass().hashCode();
    }
}
