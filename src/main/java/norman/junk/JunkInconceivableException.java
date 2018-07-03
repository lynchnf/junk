package norman.junk;

public class JunkInconceivableException extends RuntimeException {
    public JunkInconceivableException(String message) {
        super(message);
    }

    public JunkInconceivableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}