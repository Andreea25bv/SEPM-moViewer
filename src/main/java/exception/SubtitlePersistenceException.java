package exception;

/**
 * Created by Toan on 29.11.2014.
 */
public class SubtitlePersistenceException extends  Exception{
    public SubtitlePersistenceException() {
        super();
    }

    public SubtitlePersistenceException(String message) {
        super(message);
    }

    public SubtitlePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubtitlePersistenceException(Throwable cause) {
        super(cause);
    }

    protected SubtitlePersistenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
