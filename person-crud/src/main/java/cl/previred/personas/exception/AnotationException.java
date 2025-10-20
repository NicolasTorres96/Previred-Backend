package cl.previred.personas.exception;

public class AnotationException extends RuntimeException {
    private final Boolean isControlled = true;
    public AnotationException(String message) {
        super(message);
    }
}
