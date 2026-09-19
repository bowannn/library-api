package usta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.*;

import java.time.LocalDate;

/**
 * MODEL - Representa el préstamo de una cantidad de un equipo a un estudiante.
 * Relación "muchos a uno" hacia Equipment y hacia Student (según el diagrama).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    // Null mientras el préstamo sigue activo (no devuelto)
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public Loan(LocalDate loanDate, Integer quantity, Equipment equipment, Student student) {
        this.loanDate = loanDate;
        this.quantity = quantity;
        this.equipment = equipment;
        this.student = student;
    }

    @Override
    // Compara por id
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    // Hash de la clase (usado por colecciones como HashSet)
    public int hashCode() {
        return getClass().hashCode();
    }
}
