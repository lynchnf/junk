package norman.junk;

public class NewJunkException extends Exception {
    public NewJunkException(String message) {
        super(message);
    }

    public NewJunkException(String message, Throwable throwable) {
        super(message, throwable);
    }
}