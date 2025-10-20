package cl.previred.personas.repository;

import cl.previred.personas.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByRut(String rut);
    boolean existsByRut(String rut);
}
