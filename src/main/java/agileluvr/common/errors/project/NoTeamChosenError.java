package agileluvr.common.errors.project;

public class NoTeamChosenError extends RuntimeException{

    public NoTeamChosenError(){ super("User has not chosen a development team to be on"); }
}
