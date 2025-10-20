package cl.previred.personas.validation;

import cl.previred.personas.exception.AnotationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RutValidator implements ConstraintValidator<Rut, String> {
    private String message;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            message = "Rut no puede estar vacío o nulo";
            throw new AnotationException(message);
        }

        boolean validation = true;

        String regex = "\\d{7,9}[\\dkK]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        if (!matcher.matches()) {
            message = "Rut no válido ya que no corresponde el largo o el formato está incorrecto";
            throw new AnotationException(message);
        }

        value = value.toUpperCase();
        value = value.replace(".", "");
        value = value.replace("-", "");
        int rutAux = Integer.parseInt(value.substring(0, value.length() - 1));

        char dv = value.charAt(value.length() - 1);

        int m = 0;
        int s = 1;
        for (; rutAux != 0; rutAux /= 10) {
            s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
        }

        if (dv != (char) (s != 0 ? s + 47 : 75)) {
            message = "Rut no válido ya que no corresponde a dígito verificador";
            throw new AnotationException(message);
        }
        return validation;
    }
}
