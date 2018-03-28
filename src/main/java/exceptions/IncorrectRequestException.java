package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by vadim on 7/26/17.
 */
public class IncorrectRequestException extends Exception {
    public IncorrectRequestException (String message) {
        super(message);
    }
    public IncorrectRequestException (String message, Throwable cause) {
        super(message, cause);
    }

}
