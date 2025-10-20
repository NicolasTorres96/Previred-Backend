package cl.previred.personas.service;

import cl.previred.personas.domain.Address;
import cl.previred.personas.domain.Person;
import cl.previred.personas.dto.PersonRequest;
import cl.previred.personas.dto.PersonResponse;
import cl.previred.personas.exception.ControlledException;
import cl.previred.personas.repository.PersonRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepository repository;
    private final PersistLaterService persistLaterService;

    public PersonService(PersonRepository repository, PersistLaterService persistLaterService) {
        this.repository = repository;
        this.persistLaterService = persistLaterService;
    }

    @Transactional
    public PersonResponse create(PersonRequest req) {
        if (repository.existsByRut(req.rut())) {
            throw new ControlledException("Ya existe una persona con el RUT " + req.rut());
        }
        Person p = mapToEntity(req);
        try {
            p = repository.saveAndFlush(p);
            return mapToResponse(p);
        } catch (DataAccessException ex) {
            // Si la BD está caída, guardamos en la cola para persistir más tarde
            persistLaterService.enqueueCreate(req);
            throw new ControlledException("BD no disponible. La solicitud fue encolada para persistir cuando vuelva el servicio.");
        }
    }

    @Transactional(readOnly = true)
    public PersonResponse get(UUID id) {
        Person p = repository.findById(id).orElseThrow(() -> new ControlledException("Persona no encontrada"));
        return mapToResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> list() {
        return repository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public PersonResponse update(UUID id, PersonRequest req) {
        Person p = repository.findById(id).orElseThrow(() -> new ControlledException("Persona no encontrada"));
        // Si cambia el RUT, validar unicidad
        if (!p.getRut().equals(req.rut()) && repository.existsByRut(req.rut())) {
            throw new ControlledException("Ya existe una persona con el RUT " + req.rut());
        }
        p.setRut(req.rut());
        p.setNombre(req.nombre());
        p.setApellido(req.apellido());
        p.setFechaNacimiento(req.fechaNacimiento());
        p.setDireccion(Address.builder().calle(req.calle()).comuna(req.comuna()).region(req.region()).build());

        try {
            p = repository.saveAndFlush(p);
            return mapToResponse(p);
        } catch (DataAccessException ex) {
            persistLaterService.enqueueUpdate(id, req);
            throw new ControlledException("BD no disponible. La actualización fue encolada para aplicar cuando vuelva el servicio.");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ControlledException("Persona no encontrada");
        }
        try {
            repository.deleteById(id);
        } catch (DataAccessException ex) {
            persistLaterService.enqueueDelete(id);
            throw new ControlledException(
                    "BD no disponible. La eliminación fue encolada para aplicar cuando vuelva el servicio.");
        }
    }

    private Person mapToEntity(PersonRequest req) {
        return Person.builder()
                .rut(req.rut())
                .nombre(req.nombre())
                .apellido(req.apellido())
                .fechaNacimiento(req.fechaNacimiento())
                .direccion(Address.builder()
                        .calle(req.calle())
                        .comuna(req.comuna())
                        .region(req.region())
                        .build())
                .build();
    }

    private PersonResponse mapToResponse(Person p) {
        int edad = Period.between(p.getFechaNacimiento(), LocalDate.now()).getYears();
        return new PersonResponse(
                p.getId(), p.getRut(), p.getNombre(), p.getApellido(),
                p.getFechaNacimiento(), edad,
                p.getDireccion().getCalle(), p.getDireccion().getComuna(), p.getDireccion().getRegion()
        );
    }
}
