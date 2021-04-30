package luminor.homework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPaymentResourceException extends RuntimeException {

    public InvalidPaymentResourceException() {}

    public InvalidPaymentResourceException(String message) {
        super(message);
    }
}
