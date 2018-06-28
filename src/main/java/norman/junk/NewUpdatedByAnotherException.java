package norman.junk;

public class NewUpdatedByAnotherException extends NewJunkException {
    public NewUpdatedByAnotherException(String message) {
        super(message);
    }

    public NewUpdatedByAnotherException(String message, Throwable throwable) {
        super(message, throwable);
    }
}