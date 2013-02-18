package exception;

import java.io.IOException;

/**
 * This exception class is meant to indicate an error in the formatting of
 * an input record.
 */
public class MalformedInputException extends IOException {

    public MalformedInputException(String message) {
        super(message);
    }

}
