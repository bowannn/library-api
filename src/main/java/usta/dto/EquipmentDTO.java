package usta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO inmutable para crear/actualizar equipos (validación con Bean Validation)
public record EquipmentDTO(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "Máximo 30 caracteres")
        String code,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "Máximo 100 caracteres")
        String name,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) {}
