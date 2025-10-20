package cl.previred.personas.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Address {
    @NotBlank(message = "La calle es obligatoria")
    private String calle;
    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;
    @NotBlank(message = "La región es obligatoria")
    private String region;
}
