package agileluvr.controlFlow;

import agileluvr.common.documents.ProjectDocument;
import agileluvr.common.documents.ProjectListingDocument;
import agileluvr.common.documents.UserDocument;
import agileluvr.common.errors.project.AlreadyHasProjectError;
import agileluvr.common.errors.project.ProjectDoesNotExistError;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import agileluvr.common.models.ProjectListingModel;
import agileluvr.common.models.ProjectModel;
import agileluvr.project.ProjectController;
import agileluvr.project.ProjectListingController;
import agileluvr.project.ProjectListingRepository;
import agileluvr.project.ProjectRepository;
import agileluvr.user.UserController;
import agileluvr.user.UserRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import static agileluvr.common.Identifiers.ProjectIdentifier.NO_PROJECT_ASSIGNED;


// need to rewrite both project classes -- remove API calls and transfer to here
// keep user class apis -- need for creating users

@RestController
@RequestMapping("/api/projectUserMediator")
public class ProjectUserMediator {

    private final UserRepository userRepository;

    private final ProjectRepository projectRepository;

    private final ProjectListingRepository projectListingRepository;

    private final ProjectListingController projectListingController;

    private final ProjectController projectController;

    private final UserController userController;

    public ProjectUserMediator(UserRepository userRepository,
                               ProjectRepository projectRepository,
                               ProjectListingRepository projectListingRepository,
                               ProjectListingController projectListingController,
                               ProjectController projectController,
                               UserController userController){

        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectListingRepository = projectListingRepository;
        this.projectListingController = projectListingController;
        this.projectController = projectController;
        this.userController = userController;
    }


    @PostMapping("listings/post/create")
    public ProjectListingDocument createListing(@RequestBody @ApiParam(name = "Project Listing Format",
                                                value = "Project Listing to create") ProjectListingModel projectListing) {

        String uid = projectListing.getProjectCreator();

        if(!userRepository.existsById(uid)){throw new UserNotFoundError(uid);}
        if(userController.hasActiveProject(uid)) { throw new AlreadyHasProjectError(); }
        ProjectListingDocument createdListing =  this.projectListingController.createListing(projectListing);
        this.userController.changeListing(createdListing.getId(), uid);

        return createdListing;
    }


    @DeleteMapping("listings/delete/{listingID}/{uid}")
    public void deleteListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                              @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectListingDocument foundProject = this.projectListingRepository.findById(listingID)
                .orElseThrow(() -> new ProjectDoesNotExistError(listingID));

        for(String currID: foundProject.getFrontEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        for(String currID: foundProject.getBackEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
        }


        this.projectListingController.deleteListing(listingID, uid);
    }


    @PutMapping("listings/put/{teamType}/{listingID}/{uid}")
    public ProjectListingDocument joinListing(@PathVariable @ApiParam(name = "team user will join", value = "team choice") String teamType,
                                              @PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                              @PathVariable @ApiParam(name = "user id", value = "user trying to join project") String uid) {

        ProjectListingDocument listingWithAddedUser =  this.projectListingController.joinListing(teamType, listingID, uid);
        this.userController.changeListing(listingWithAddedUser.getId(), uid);

        return listingWithAddedUser;
    }

    @PutMapping("listings/put/{listingID}/{uid}")
    public ProjectListingDocument removeFromListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                                    @PathVariable @ApiParam(name = "user id", value = "user trying to join project") String uid ){

        ProjectListingDocument listingWithRemovedUser = this.projectListingController.removeFromListing(listingID, uid);
        this.userController.changeListing(NO_PROJECT_ASSIGNED, uid);

        return listingWithRemovedUser;
    }

    @PostMapping("projects/post/create")
    public ProjectDocument createProject(String uid, String projectListingID, @RequestBody ProjectModel projectModel){

        // this really isnt needed, just a catch error
        if(!userRepository.existsById(uid)){throw new UserNotFoundError(uid);}

        if(!projectListingController.confirmUserOwnership(projectListingID, uid)) { throw new UserNotOwnerOfProjectError(uid, projectListingID); }

        ProjectDocument createdProject = projectController.createProject(projectModel);

        // set people on projectlisting to have new project document ID
        for(String currID : createdProject.getFrontEndMemberList()){
            this.userController.changeListing(createdProject.getId(), currID);
        }

        for(String currID : createdProject.getBackEndMemberList()){
            this.userController.changeListing(createdProject.getId(), currID);
        }

        // delete projectlisting, no need for it anymore
        this.projectListingController.deleteListing(projectListingID, uid);

        return createdProject;

    }

    @PutMapping("projects/put/{projectID}/{uid}")
    public ProjectDocument completeProject(@PathVariable @ApiParam(name ="Project id", value = "Project to be completed") String projectID,
                                           @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument completedProject = this.projectController.completeProject(projectID, uid);

        for(String currID: completedProject.getFrontEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
            this.userController.addCompletedProject(projectID, uid);
        }

        for(String currID: completedProject.getBackEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
            this.userController.addCompletedProject(projectID, uid);
        }


        return completedProject;
    }

    @DeleteMapping("projects/delete/{projectID}/{uid}")
    public void markAsFailure(@PathVariable @ApiParam(name ="Project id", value = "Project to be deleted") String projectID,
                              @PathVariable @ApiParam(name = "id", value = "user id") String uid){

        ProjectDocument foundProject = this.projectRepository.findById(projectID)
                .orElseThrow(()-> new ProjectDoesNotExistError(projectID));

        for(String currID: foundProject.getFrontEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        for(String currID: foundProject.getBackEndMemberList()){
            this.userController.changeListing(NO_PROJECT_ASSIGNED, currID);
        }

        this.projectController.markAsFailure(projectID, uid);
    }



    // when a project listing turns int oa project, we must update every user on that project listing to have that their current porject is a new ID

}
