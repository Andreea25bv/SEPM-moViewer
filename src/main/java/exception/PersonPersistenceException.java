package exception;

/**
 * Created by Toan on 29.11.2014.
 */
public class PersonPersistenceException extends Exception{

    public PersonPersistenceException() {
        super();
    }

    public PersonPersistenceException(String message) {
        super(message);
    }

    public PersonPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonPersistenceException(Throwable cause) {
        super(cause);
    }

    protected PersonPersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
