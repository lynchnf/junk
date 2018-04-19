package norman.junk;

public class JunkException extends Exception {
    public JunkException(String message) {
        super(message);
    }

    public JunkException(String message, Throwable throwable) {
        super(message, throwable);
    }
}