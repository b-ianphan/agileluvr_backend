package agileluvr.common.errors.project;

public class TeamSizeOutOfBoundsError extends RuntimeException{

    public TeamSizeOutOfBoundsError(){ super("Team limit must be greater than 1 and less than 6"); }
};
