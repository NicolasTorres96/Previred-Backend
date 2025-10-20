package cl.previred.personas.exception.handlers;

import cl.previred.personas.exception.ControlledException;
import cl.previred.personas.viewmodel.Wrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControlledExeptionHandler {

    @ExceptionHandler(ControlledException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Wrapper> handleControlledExeption(ControlledException ex){
        Wrapper response = new Wrapper();
        response.setData(null);
        response.setDescripcion(ex.getMessage());
        response.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
