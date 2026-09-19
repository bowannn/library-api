package usta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * MODEL - Representa un miembro que puede tomar prestados varios libros.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 120)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "member_books",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> borrowedBooks = new HashSet<>();

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Añade un libro a los prestados del miembro
    public void addBook(Book book) {
        this.borrowedBooks.add(book);
    }

    // Quita un libro de los prestados del miembro
    public void removeBook(Book book) {
        this.borrowedBooks.remove(book);
    }

    // Cuenta libros prestados
    public int getBorrowedCount() {
        return this.borrowedBooks.size();
    }

    // Compara por id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    // Hash de la clase (usado por colecciones como HashSet)
    public int hashCode() {
        return getClass().hashCode();
    }
}
