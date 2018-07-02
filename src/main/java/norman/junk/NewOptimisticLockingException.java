package norman.junk;

public class NewOptimisticLockingException extends NewJunkException {
    public NewOptimisticLockingException(String message) {
        super(message);
    }

    public NewOptimisticLockingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}