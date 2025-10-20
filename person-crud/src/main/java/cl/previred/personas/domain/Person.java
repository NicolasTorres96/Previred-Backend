package cl.previred.personas.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "personas", indexes = {
        @Index(name = "idx_persona_rut", columnList = "rut", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Person {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 12)
    @NotBlank
    private String rut;

    @Column(nullable = false)
    @NotBlank
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    private String apellido;

    @Column(nullable = false)
    @NotNull
    private LocalDate fechaNacimiento;

    @Embedded
    @Valid
    private Address direccion;
}
