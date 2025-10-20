package cl.previred.personas.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String rut,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        int edad,
        String calle,
        String comuna,
        String region
) {}
