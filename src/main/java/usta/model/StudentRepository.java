package usta.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {
    // Lista todos
    public List<Student> listAllStudents() {
        return listAll();
    }

    // Busca por id
    public Optional<Student> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    // Guarda
    public Student save(Student student) {
        persist(student);
        return student;
    }

    // Elimina por id
    public boolean delete(Long id) {
        return deleteById(id);
    }
}
