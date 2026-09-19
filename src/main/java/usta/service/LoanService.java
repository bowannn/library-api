package usta.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import usta.dto.LoanDTO;
import usta.model.Equipment;
import usta.model.EquipmentRepository;
import usta.model.Loan;
import usta.model.LoanRepository;
import usta.model.Student;
import usta.model.StudentRepository;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LoanService {
    @Inject
    LoanRepository repository;

    @Inject
    EquipmentRepository equipmentRepository;

    @Inject
    StudentRepository studentRepository;

    // Lista todos
    public List<Loan> findAll() {
        return repository.listAllLoans();
    }

    // Busca por id
    public Loan findById(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Préstamo no encontrado"));
    }

    @Transactional
    // Crea el préstamo: valida que el equipo y el estudiante existan y descuenta el stock
    public Loan create(LoanDTO dto) {
        Equipment equipment = equipmentRepository.findByIdOptional(dto.equipmentId())
                .orElseThrow(() -> new NotFoundException("Equipo no encontrado"));
        Student student = studentRepository.findByIdOptional(dto.studentId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        if (equipment.getStock() == null || equipment.getStock() < dto.quantity()) {
            throw new BadRequestException("Sin stock disponible para el equipo: " + equipment.getName());
        }

        equipment.setStock(equipment.getStock() - dto.quantity());
        equipmentRepository.persist(equipment);

        Loan loan = new Loan(dto.loanDate(), dto.quantity(), equipment, student);
        repository.persist(loan);
        return loan;
    }

    @Transactional
    // Actualiza un préstamo activo (fecha y cantidad); ajusta el stock si cambia la cantidad
    public Loan update(Long id, LoanDTO dto) {
        Loan existing = findById(id);
        if (existing.getReturnDate() != null) {
            throw new BadRequestException("No se puede modificar un préstamo ya devuelto");
        }
        if (!existing.getEquipment().getId().equals(dto.equipmentId())) {
            throw new BadRequestException("No se puede cambiar el equipo de un préstamo existente");
        }
        if (!existing.getStudent().getId().equals(dto.studentId())) {
            throw new BadRequestException("No se puede cambiar el estudiante de un préstamo existente");
        }

        Equipment equipment = existing.getEquipment();
        int diff = dto.quantity() - existing.getQuantity();
        if (diff > 0 && (equipment.getStock() == null || equipment.getStock() < diff)) {
            throw new BadRequestException("Sin stock disponible para el equipo: " + equipment.getName());
        }
        equipment.setStock(equipment.getStock() - diff);
        equipmentRepository.persist(equipment);

        existing.setQuantity(dto.quantity());
        existing.setLoanDate(dto.loanDate());
        repository.persist(existing);
        return existing;
    }

    @Transactional
    // Elimina el préstamo (si seguía activo, primero repone el stock)
    public void delete(Long id) {
        Loan loan = findById(id);
        if (loan.getReturnDate() == null) {
            Equipment equipment = loan.getEquipment();
            equipment.setStock(equipment.getStock() + loan.getQuantity());
            equipmentRepository.persist(equipment);
        }
        repository.delete(loan);
    }

    @Transactional
    // Marca el préstamo como devuelto y repone el stock del equipo
    public Loan devolver(Long id) {
        Loan loan = findById(id);
        if (loan.getReturnDate() != null) {
            throw new BadRequestException("El préstamo ya fue devuelto");
        }
        loan.setReturnDate(LocalDate.now());
        Equipment equipment = loan.getEquipment();
        equipment.setStock(equipment.getStock() + loan.getQuantity());
        equipmentRepository.persist(equipment);
        repository.persist(loan);
        return loan;
    }
}
