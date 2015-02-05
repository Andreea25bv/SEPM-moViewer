package exception;

/**
 * Created by Toan on 29.11.2014.
 */
public class MoviePersistenceException extends Exception {
    public MoviePersistenceException() {
        super();
    }

    public MoviePersistenceException(String message) {
        super(message);
    }

    public MoviePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoviePersistenceException(Throwable cause) {
        super(cause);
    }

    protected MoviePersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
