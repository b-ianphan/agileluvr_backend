package agileluvr.controlFlow;

import agileluvr.common.documents.ProjectDocument;
import agileluvr.common.documents.ProjectListingDocument;
import agileluvr.common.errors.project.AlreadyHasProjectError;
import agileluvr.common.errors.project.ProjectDoesNotExistError;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import agileluvr.common.models.ProjectListingModel;
import agileluvr.common.models.ProjectModel;
import agileluvr.project.ProjectService;
import agileluvr.project.ProjectListingService;
import agileluvr.project.ProjectListingRepository;
import agileluvr.user.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import static agileluvr.common.Identifiers.ProjectIdentifier.NO_PROJECT_ASSIGNED;


// need to rewrite both project classes -- remove API calls and transfer to here
// keep user class apis -- need for creating users

@RestController
@RequestMapping("/api/projectUserMediator")
public class ProjectUserMediator {


    private final ProjectListingService projectListingService;

    private final ProjectService projectService;

    private final UserService userService;

    public ProjectUserMediator (ProjectListingService projectListingService,
                               ProjectService projectService,
                               UserService userService){

        this.projectListingService = projectListingService;
        this.projectService = projectService;
        this.userService = userService;
    }


    @PostMapping("listings/post/create")
    public ProjectListingDocument createListing(@RequestBody @ApiParam(name = "Project Listing Format",
                                                value = "Project Listing to create") ProjectListingModel projectListing) {

        String uid = projectListing.getProjectCreator();

        if(!this.userService.exists(uid)){throw new UserNotFoundError(uid);}
        if(userService.hasActiveProject(uid)) { throw new AlreadyHasProjectError(); }
        ProjectListingDocument createdListing =  this.projectListingService.createListing(projectListing);
        this.userService.changeListing(createdListing.getId(), uid);

        return createdListing;
    }


    @DeleteMapping("listings/delete/{listingID}/{uid}")
    public void deleteListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                              @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectListingDocument foundProject = this.projectListingService.findProject(listingID);

        for(String currID: foundProject.getFrontEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        for(String currID: foundProject.getBackEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
        }


        this.projectListingService.deleteListing(listingID, uid);
    }


    @PutMapping("listings/put/{teamType}/{listingID}/{uid}")
    public ProjectListingDocument joinListing(@PathVariable @ApiParam(name = "team user will join", value = "team choice") String teamType,
                                              @PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                              @PathVariable @ApiParam(name = "user id", value = "user trying to join project") String uid) {

        ProjectListingDocument listingWithAddedUser =  this.projectListingService.joinListing(teamType, listingID, uid);
        this.userService.changeListing(listingWithAddedUser.getId(), uid);

        return listingWithAddedUser;
    }

    @PutMapping("listings/put/{listingID}/{uid}")
    public ProjectListingDocument removeFromListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                                    @PathVariable @ApiParam(name = "user id", value = "user trying to join project") String uid ){

        ProjectListingDocument listingWithRemovedUser = this.projectListingService.removeFromListing(listingID, uid);
        this.userService.changeListing(NO_PROJECT_ASSIGNED, uid);

        return listingWithRemovedUser;
    }

    @PostMapping("projects/post/create")
    public ProjectDocument createProject(String uid, String projectListingID, @RequestBody ProjectModel projectModel){

        // this really isnt needed, just a catch error
        if(!this.userService.exists(uid)){throw new UserNotFoundError(uid);}

        if(!projectListingService.confirmUserOwnership(projectListingID, uid)) { throw new UserNotOwnerOfProjectError(uid, projectListingID); }

        ProjectDocument createdProject = projectService.createProject(projectModel);

        // set people on projectlisting to have new project document ID
        for(String currID : createdProject.getFrontEndMemberList()){
            this.userService.changeListing(createdProject.getId(), currID);
        }

        for(String currID : createdProject.getBackEndMemberList()){
            this.userService.changeListing(createdProject.getId(), currID);
        }

        // delete projectlisting, no need for it anymore
        this.projectListingService.deleteListing(projectListingID, uid);

        return createdProject;

    }

    @PutMapping("projects/put/{projectID}/{uid}")
    public ProjectDocument completeProject(@PathVariable @ApiParam(name ="Project id", value = "Project to be completed") String projectID,
                                           @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument completedProject = this.projectService.completeProject(projectID, uid);

        for(String currID: completedProject.getFrontEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
            this.userService.addCompletedProject(projectID, uid);
        }

        for(String currID: completedProject.getBackEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
            this.userService.addCompletedProject(projectID, uid);
        }


        return completedProject;
    }

    @DeleteMapping("projects/delete/{projectID}/{uid}")
    public void markAsFailure(@PathVariable @ApiParam(name ="Project id", value = "Project to be deleted") String projectID,
                              @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument foundProject = this.projectService.findProject(projectID);

        for(String currID: foundProject.getFrontEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        for(String currID: foundProject.getBackEndMemberList()){
            this.userService.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        this.projectService.markAsFailure(projectID, uid);
    }


}
