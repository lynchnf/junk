package norman.junk;

public class NewInconceivableException extends RuntimeException {
    public NewInconceivableException(String message) {
        super(message);
    }

    public NewInconceivableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}