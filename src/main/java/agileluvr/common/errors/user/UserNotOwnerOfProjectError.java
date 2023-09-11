package agileluvr.common.errors.user;

public class UserNotOwnerOfProjectError extends RuntimeException{
    public UserNotOwnerOfProjectError(String uid, String projectID) { super("User " + uid +" is not the owner of project "+ projectID); }
}
