package agileluvr.project;


import agileluvr.common.documents.ProjectDocument;
import agileluvr.common.errors.project.ProjectDoesNotExistError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import agileluvr.common.models.ProjectModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;


@Service
@ApiOperation("Started Projects API")
public class ProjectController {

    private final ProjectRepository projects;

    public ProjectController(ProjectRepository projects){ this.projects = projects; }

    public ProjectDocument createProject(@ApiParam(name ="Project Format",
                                                    value = "Project to create, based off project listing")
                                                    ProjectModel projectModel) {

            return projects.save(ProjectDocument.builder()
                    .projectCreator(projectModel.getProjectCreator())
                    .projectName(projectModel.getProjectName())
                    .projectDescription(projectModel.getProjectDescription())
                    .frontEndMemberList(projectModel.getFrontEndMemberList())
                    .backEndMemberList(projectModel.getBackEndMemberList())
                    .projectManager(projectModel.getProjectManager())
                    .build()
            );
    }


    public ProjectDocument completeProject(@ApiParam(name ="Project id", value = "Project to be completed") String projectID,
                                            @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument foundProject = projects.findById(projectID)
                .orElseThrow(() -> new ProjectDoesNotExistError(projectID));

        if(!confirmUserOwnership(projectID, uid)) { throw new UserNotOwnerOfProjectError(uid, projectID); }

        foundProject.setInProgress(false);

        return projects.save(foundProject);
    }

    @DeleteMapping()
    public void markAsFailure(@ApiParam(name ="Project id", value = "Project to be deleted") String projectID,
                              @ApiParam(name = "id", value = "user id") String uid){

        if(!projects.existsById(projectID)) { throw new ProjectDoesNotExistError(projectID); }

        if(!confirmUserOwnership(projectID, uid)) { throw new UserNotOwnerOfProjectError(uid, projectID); }

        projects.deleteById(projectID);
    }

    public boolean confirmUserOwnership(@ApiParam(name ="Project id", value = "Project") String projectID,
                                        @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument foundProject = projects.findById(projectID)
                .orElseThrow(() -> new ProjectDoesNotExistError(projectID));

        return foundProject.getProjectCreator().equals(uid);

    }



}
