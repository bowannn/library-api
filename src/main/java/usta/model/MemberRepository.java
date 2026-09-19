package usta.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MemberRepository implements PanacheRepository<Member> {
    // Lista todos
    public List<Member> listAllMembers() {
        return listAll();
    }

    // Busca por id
    public Optional<Member> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    // Guarda
    public Member save(Member member) {
        persist(member);
        return member;
    }

    // Elimina por id
    public boolean delete(Long id) {
        return deleteById(id);
    }
}