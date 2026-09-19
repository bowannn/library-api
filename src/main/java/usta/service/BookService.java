package usta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import usta.dto.BookDTO;
import usta.model.Book;
import usta.model.BookRepository;
import usta.model.Member;
import usta.model.MemberRepository;

import java.util.List;

@ApplicationScoped
public class BookService {
    @Inject
    BookRepository repository;

    @Inject
    MemberRepository memberRepository;

    // Lista todos
    public List<Book> findAll() {
        return repository.listAllBooks();
    }

    // Busca por id
    public Book findById(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Libro no encontrado"));
    }

    @Transactional
    // Crea desde DTO (validado con @Valid en el Resource)
    public Book create(BookDTO dto) {
        Book book = new Book(dto.title(), dto.author(), dto.isbn(), dto.stock(), dto.price());
        repository.persist(book);
        return book;
    }

    @Transactional
    // Actualiza desde DTO
    public Book update(Long id, BookDTO dto) {
        Book existing = findById(id);
        existing.setTitle(dto.title());
        existing.setAuthor(dto.author());
        existing.setIsbn(dto.isbn());
        existing.setStock(dto.stock());
        existing.setPrice(dto.price());
        repository.persist(existing);
        return existing;
    }

    @Transactional
    // Elimina por id (limpia préstamos primero)
    public void delete(Long id) {
        Book book = findById(id);
        List<Member> members = memberRepository.listAll();
        for (Member member : members) {
            if (member.getBorrowedBooks().removeIf(b -> b.getId().equals(id))) {
                memberRepository.persist(member);
            }
        }
        repository.delete(book);
    }
}
