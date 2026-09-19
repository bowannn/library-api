package usta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import usta.dto.MemberDTO;
import usta.model.Book;
import usta.model.BookRepository;
import usta.model.Member;
import usta.model.MemberRepository;

import java.util.List;

@ApplicationScoped
public class MemberService {
    @Inject
    MemberRepository repository;

    @Inject
    BookRepository bookRepository;

    // Lista todos
    public List<Member> findAll() {
        return repository.listAllMembers();
    }

    // Busca por id
    public Member findById(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Miembro no encontrado"));
    }

    @Transactional
    // Crea desde DTO
    public Member create(MemberDTO dto) {
        Member member = new Member(dto.name(), dto.email());
        repository.persist(member);
        return member;
    }

    @Transactional
    // Actualiza desde DTO
    public Member update(Long id, MemberDTO dto) {
        Member existing = findById(id);
        existing.setName(dto.name());
        existing.setEmail(dto.email());
        repository.persist(existing);
        return existing;
    }

    @Transactional
    // Elimina por id
    public void delete(Long id) {
        if (!repository.deleteById(id)) throw new NotFoundException("Miembro no encontrado");
    }

    @Transactional
    // Presta un libro al miembro (descuenta del stock)
    public Member borrowBook(Long memberId, Long bookId) {
        Member member = findById(memberId);
        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));
        if (member.getBorrowedBooks().contains(book)) {
            throw new BadRequestException("El miembro ya tiene prestado este libro");
        }
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new BadRequestException("Sin stock disponible para el libro: " + book.getTitle());
        }
        book.setStock(book.getStock() - 1);
        bookRepository.persist(book);
        member.addBook(book);
        repository.persist(member);
        return member;
    }

    @Transactional
    // Devuelve un libro del miembro (suma al stock)
    public Member returnBook(Long memberId, Long bookId) {
        Member member = findById(memberId);
        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Libro no encontrado"));
        if (!member.getBorrowedBooks().contains(book)) {
            throw new BadRequestException("El miembro no tiene prestado este libro");
        }
        member.removeBook(book);
        repository.persist(member);
        book.setStock(book.getStock() + 1);
        bookRepository.persist(book);
        return member;
    }
}
