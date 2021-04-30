package luminor.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCsvException extends RuntimeException {

    public InvalidCsvException() {}

    public InvalidCsvException(String message) {
        super(message);
    }
}
