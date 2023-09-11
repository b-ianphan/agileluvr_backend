package agileluvr.common.errors.project;

public class ProjectDoesNotExistError extends RuntimeException{

    public ProjectDoesNotExistError(String projectID){ super("The project being referenced does not exist. Project ID "+ projectID); }
}
