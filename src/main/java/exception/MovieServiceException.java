package exception;

/**
 * Created by Ali on 13/12/14.
 */
public class MovieServiceException extends Exception {

    public MovieServiceException() {
        super();
    }

    public MovieServiceException(String message) {
        super(message);
    }

    public MovieServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MovieServiceException(Throwable cause) {
        super(cause);
    }

    protected MovieServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
