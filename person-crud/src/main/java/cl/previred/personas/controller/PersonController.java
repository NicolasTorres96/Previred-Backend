package cl.previred.personas.controller;

import cl.previred.personas.dto.PersonRequest;
import cl.previred.personas.dto.PersonResponse;
import cl.previred.personas.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody PersonRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public PersonResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<PersonResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public PersonResponse update(@PathVariable UUID id, @Valid @RequestBody PersonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
