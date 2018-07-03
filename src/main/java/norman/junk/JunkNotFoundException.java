package norman.junk;

public class JunkNotFoundException extends JunkException {
    public JunkNotFoundException(String message) {
        super(message);
    }

    public JunkNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}