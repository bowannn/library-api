package usta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// DTO inmutable para crear/actualizar préstamos
public record LoanDTO(
        @NotNull(message = "La fecha del préstamo es obligatoria")
        LocalDate loanDate,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
        Integer quantity,

        @NotNull(message = "El id del equipo es obligatorio")
        Long equipmentId,

        @NotNull(message = "El id del estudiante es obligatorio")
        Long studentId
) {}
