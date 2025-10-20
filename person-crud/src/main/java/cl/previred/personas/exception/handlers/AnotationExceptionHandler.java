package cl.previred.personas.exception.handlers;

import cl.previred.personas.exception.AnotationException;
import cl.previred.personas.viewmodel.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnotationExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotationExceptionHandler.class);
    @ExceptionHandler(AnotationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Wrapper> handleAnotationException(AnotationException ex){
        ex.printStackTrace();
        LOGGER.error(ex.getMessage(),ex);
        Wrapper response = new Wrapper();
        response.setData(null);
        response.setDescripcion(ex.getMessage());
        response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
