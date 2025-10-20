package cl.previred.personas.dto;

import cl.previred.personas.validation.Rut;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PersonRequest(
        @Rut String rut,
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotBlank String calle,
        @NotBlank String comuna,
        @NotBlank String region
) {}
