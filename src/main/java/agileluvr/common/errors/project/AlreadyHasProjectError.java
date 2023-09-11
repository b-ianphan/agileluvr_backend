package agileluvr.common.errors.project;

public class AlreadyHasProjectError extends RuntimeException{
    public AlreadyHasProjectError(){ super("User already has a project"); }
}
