package norman.junk;

public class DatabaseException extends JunkException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}