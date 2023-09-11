package agileluvr.common.errors.user;

public class UserNotFoundError extends RuntimeException{
    public UserNotFoundError(String userLogin) {
        super("could not find user " + userLogin);
    }
}
