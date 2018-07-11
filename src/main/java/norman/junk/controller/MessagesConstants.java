package norman.junk.controller;

public class MessagesConstants {
    public static final String OPTIMISTIC_LOCK_ERROR = "%s with id=%d was updated or deleted by another transaction.";
    public static final String MULTI_OPTIMISTIC_LOCK_ERROR = "One or more %s were updated or deleted by another transaction.";
    public static final String NOT_FOUND_ERROR = "%s was not found for id=%d.";
    public static final String PARENT_ID_NEEDED_FOR_ADD_ERROR = "When adding a new %s, a valid %s id must be provided.";
    public static final String UPLOADED_FILE_NOT_FOUND_ERROR = "Upload file is empty or missing.";
    public static final String UPLOADED_FILE_READ_ERROR = "Unable to read from uploaded file %s.";
    public static final String RECONCILE_ERROR = "Account not able to reconciled, difference=%.2f.";
    public static final String UNEXPECTED_ERROR = "UNEXPECTED ERROR";
    public static final String SUCCESSFULLY_ADDED = "%s successfully added, id=%d.";
    public static final String SUCCESSFULLY_UPDATED = "%s successfully updated, id=%d.";
    public static final String SUCCESSFULLY_UPDATED_MULTI = "%s successfully updated.";
    public static final String SUCCESSFULLY_ASSIGNED_CATEGORIES = "Categories successfully assigned to %d Transactions.";
    public static final String SUCCESSFULLY_UPLOADED_TRANS = "Account successfully updated with %d Transactions, acct id=%d.";
    public static final String SUCCESSFULLY_RECONCILED = "Account successfully reconciled, acct id=%d.";

    private MessagesConstants() {
    }
}