package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by vadim on 8/1/17.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Incorrectly constructed request.")
public class BioServerException extends Exception {
    public BioServerException(String message) {
        super(message);
    }
    public BioServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
