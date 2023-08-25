package agileluvr.server.common.errors;

public class UserNotFoundError extends RuntimeException{
    public UserNotFoundError(String userLogin) {
        super("could not find user " + userLogin);
    }
}
