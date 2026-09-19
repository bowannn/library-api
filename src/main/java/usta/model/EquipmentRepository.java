package usta.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EquipmentRepository implements PanacheRepository<Equipment> {
    // Lista todos
    public List<Equipment> listAllEquipment() {
        return listAll();
    }

    // Busca por id
    public Optional<Equipment> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }

    // Guarda
    public Equipment save(Equipment equipment) {
        persist(equipment);
        return equipment;
    }

    // Elimina por id
    public boolean delete(Long id) {
        return deleteById(id);
    }
}
