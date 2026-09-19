package usta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

// DTO inmutable para crear/actualizar libros (validación con Bean Validation)
public record BookDTO(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "Máximo 150 caracteres")
        String title,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 100)
        String author,

        @NotBlank(message = "El ISBN es obligatorio")
        @Size(max = 20)
        String isbn,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal price
) {}
