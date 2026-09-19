package usta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import usta.dto.StudentDTO;
import usta.model.LoanRepository;
import usta.model.Student;
import usta.model.StudentRepository;

import java.util.List;

@ApplicationScoped
public class StudentService {
    @Inject
    StudentRepository repository;

    @Inject
    LoanRepository loanRepository;

    // Lista todos
    public List<Student> findAll() {
        return repository.listAllStudents();
    }

    // Busca por id
    public Student findById(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
    }

    @Transactional
    // Crea desde DTO (validado con @Valid en el Resource)
    public Student create(StudentDTO dto) {
        Student student = new Student(dto.name(), dto.email(), dto.program());
        repository.persist(student);
        return student;
    }

    @Transactional
    // Actualiza desde DTO
    public Student update(Long id, StudentDTO dto) {
        Student existing = findById(id);
        existing.setName(dto.name());
        existing.setEmail(dto.email());
        existing.setProgram(dto.program());
        repository.persist(existing);
        return existing;
    }

    @Transactional
    // Elimina por id (rechaza si el estudiante tiene préstamos asociados)
    public void delete(Long id) {
        Student student = findById(id);
        if (loanRepository.existsByStudentId(id)) {
            throw new BadRequestException("No se puede eliminar el estudiante: tiene préstamos asociados");
        }
        repository.delete(student);
    }
}
