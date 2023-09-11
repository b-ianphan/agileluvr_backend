package agileluvr.common.errors.user;

public class NotAuthorizedError extends RuntimeException {
    public NotAuthorizedError() {
        super("not authorized to use this application.");
    }
}
