package norman.junk;

public class JunkOfxParseException extends JunkException {
    public JunkOfxParseException(String message) {
        super(message);
    }

    public JunkOfxParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}