package norman.junk;

public class NewNotFoundException extends NewJunkException {
    public NewNotFoundException(String message) {
        super(message);
    }

    public NewNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}