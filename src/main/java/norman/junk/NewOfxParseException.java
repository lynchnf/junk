package norman.junk;

public class NewOfxParseException extends NewJunkException {
    public NewOfxParseException(String message) {
        super(message);
    }

    public NewOfxParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}