package norman.junk.controller;

public class MessagesConstants {
    public static final String OPTIMISTIC_LOCK_ERROR = "%s with id=%d was updated or deleted by another transaction.";
    public static final String MULTI_OPTIMISTIC_LOCK_ERROR = "One or more %s were updated or deleted by another transaction.";
    public static final String NOT_FOUND_ERROR = "%s was not found for id=%d.";
    public static final String SUCCESSFULLY_ADDED = "%s successfully added, id=%d.";
    public static final String SUCCESSFULLY_UPDATED = "%s successfully updated, id=%d";
    public static final String UNEXPECTED_ERROR = "UNEXPECTED ERROR";

    private MessagesConstants() {
    }
}