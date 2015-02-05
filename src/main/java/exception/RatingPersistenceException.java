package exception;

/**
 * Created by Toan on 29.11.2014.
 */
public class RatingPersistenceException extends  Exception {
    public RatingPersistenceException() {
        super();
    }

    public RatingPersistenceException(String message) {
        super(message);
    }

    public RatingPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RatingPersistenceException(Throwable cause) {
        super(cause);
    }

    protected RatingPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
