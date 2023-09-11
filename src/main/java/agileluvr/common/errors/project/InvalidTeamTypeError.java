package agileluvr.common.errors.project;

public class InvalidTeamTypeError extends RuntimeException{

    public InvalidTeamTypeError(String teamType) { super("Team type does not exist " + teamType); }
}
