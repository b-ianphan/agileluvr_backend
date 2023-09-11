package agileluvr.common.errors.project;

public class TeamSizeDiscrepancyError extends RuntimeException{

    public TeamSizeDiscrepancyError(){ super("Team sizes do not match total team limit"); }
}
