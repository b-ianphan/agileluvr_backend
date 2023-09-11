package agileluvr.common.errors.project;

public class TeamFullError extends RuntimeException{

    public TeamFullError(String teamLocation){ super("Not enough space available to join "+ teamLocation); }
}
