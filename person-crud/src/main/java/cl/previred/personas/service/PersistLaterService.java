package cl.previred.personas.service;

import cl.previred.personas.domain.Address;
import cl.previred.personas.domain.Person;
import cl.previred.personas.dto.PersonRequest;
import cl.previred.personas.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class PersistLaterService {

    private final Path queueDir;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final PersonRepository repository;

    public PersistLaterService(PersonRepository repository,
                               @Value("${fallback.queueDir}") String queueDir) throws IOException {
        this.repository = repository;
        this.queueDir = Paths.get(queueDir);
        Files.createDirectories(this.queueDir);
    }

    public void enqueueCreate(PersonRequest req) {
        writeRecord(Map.of(
                "op", "CREATE",
                "payload", req
        ));
    }

    public void enqueueUpdate(UUID id, PersonRequest req) {
        writeRecord(Map.of(
                "op", "UPDATE",
                "id", id.toString(),
                "payload", req
        ));
    }

    public void enqueueDelete(UUID id) {
        writeRecord(Map.of(
                "op", "DELETE",
                "id", id.toString()
        ));
    }

    private void writeRecord(Object obj) {
        try {
            String name = "evt_" + Instant.now().toEpochMilli() + "_" + UUID.randomUUID() + ".json";
            Path file = queueDir.resolve(name);
            Files.writeString(file, mapper.writeValueAsString(obj), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
        }
    }

    // Reintenta cada 10s por defecto (configurable)
    @Scheduled(fixedDelayString = "${fallback.retryIntervalMs:10000}")
    public void drainQueue() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(queueDir, "*.json")) {
            for (Path file : stream) {
                try {
                    Map<?,?> record = mapper.readValue(Files.readString(file), Map.class);
                    String op = (String) record.get("op");
                    switch (op) {
                        case "CREATE" -> persistCreate(record);
                        case "UPDATE" -> persistUpdate(record);
                        case "DELETE" -> persistDelete(record);
                    }
                    Files.deleteIfExists(file);
                } catch (DataAccessException ex) {
                    // Si la BD sigue caída, detenemos el ciclo y probamos en el siguiente tick.
                    return;
                } catch (Exception ex) {
                    // Archivo corrupto o datos inválidos => descartar
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private void persistCreate(Map<?,?> record) {
        Map<?,?> payload = (Map<?,?>) record.get("payload");
        Person p = new Person();
        p.setRut((String) payload.get("rut"));
        p.setNombre((String) payload.get("nombre"));
        p.setApellido((String) payload.get("apellido"));
        p.setFechaNacimiento(java.time.LocalDate.parse((String) payload.get("fechaNacimiento")));
        p.setDireccion(new Address((String) payload.get("calle"), (String) payload.get("comuna"), (String) payload.get("region")));
        repository.saveAndFlush(p);
    }

    private void persistUpdate(Map<?,?> record) {
        UUID id = UUID.fromString((String) record.get("id"));
        Map<?,?> payload = (Map<?,?>) record.get("payload");
        Person p = repository.findById(id).orElse(null);
        if (p == null) return; // si no existe, descartamos
        p.setRut((String) payload.get("rut"));
        p.setNombre((String) payload.get("nombre"));
        p.setApellido((String) payload.get("apellido"));
        p.setFechaNacimiento(java.time.LocalDate.parse((String) payload.get("fechaNacimiento")));
        p.setDireccion(new Address((String) payload.get("calle"), (String) payload.get("comuna"), (String) payload.get("region")));
        repository.saveAndFlush(p);
    }

    private void persistDelete(Map<?,?> record) {
        UUID id = UUID.fromString((String) record.get("id"));
        repository.deleteById(id);
    }
}
