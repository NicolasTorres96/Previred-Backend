package cl.previred.personas.exception;

public class ControlledException extends RuntimeException {
    private final Boolean isControlled = true;
    public ControlledException(String message) {
        super(message);
    }
}
