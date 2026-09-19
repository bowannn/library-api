package usta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO inmutable para crear/actualizar miembros
public record MemberDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String name,

        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 120)
        @Email(message = "El correo debe ser válido")
        String email

) {}
