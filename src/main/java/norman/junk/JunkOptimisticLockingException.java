package norman.junk;

public class JunkOptimisticLockingException extends JunkException {
    public JunkOptimisticLockingException(String message) {
        super(message);
    }

    public JunkOptimisticLockingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}