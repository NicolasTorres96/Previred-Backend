package cl.previred.personas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PersonCrudApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonCrudApplication.class, args);
    }
}
