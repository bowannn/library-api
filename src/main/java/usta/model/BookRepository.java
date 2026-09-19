package usta.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    // Lista todos
    public List<Book> listAllBooks() {
        return listAll();
    }

    // Busca por id
    public Optional<Book> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    // Guarda
    public Book save(Book book) {
        persist(book);
        return book;
    }

    // Elimina por id
    public boolean delete(Long id) {
        return deleteById(id);
    }
}