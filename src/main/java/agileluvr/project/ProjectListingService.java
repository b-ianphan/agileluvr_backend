package agileluvr.project;

import agileluvr.common.documents.ProjectListingDocument;
import agileluvr.common.errors.project.*;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import agileluvr.common.models.ProjectListingModel;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static agileluvr.common.Identifiers.UserTeamIdentifier.BACK_END;
import static agileluvr.common.Identifiers.UserTeamIdentifier.FRONT_END;
import static agileluvr.common.constraints.TeamSizeConstraints.MAXIMUM_TEAM_MEMBERS;
import static agileluvr.common.constraints.TeamSizeConstraints.MINIMUM_TEAM_MEMBERS;


@Service
public class ProjectListingService {

    private final ProjectListingRepository projectListings;

    @Autowired
    public ProjectListingService(ProjectListingRepository projectListings){this.projectListings = projectListings;}



    /*
        @ApiResponse(code = 200, message = "Successfully added project to users completed list"),
        @ApiResponse(code = 400, message = "Team size limit must be within 1-6"),
        @ApiResponse(code = 406, message = "Invalid team type has been chosen by creator"),
        @ApiResponse(code = 409, message = "Front end and Back end team size do not match full team size")
     */


    public ProjectListingDocument createListing(ProjectListingModel projectListing){

        if(projectListing.getTeamSizeLimit() < MINIMUM_TEAM_MEMBERS || projectListing.getTeamSizeLimit() > MAXIMUM_TEAM_MEMBERS){
            throw new TeamSizeOutOfBoundsError();
        }

        if(projectListing.getFrontEndSizeLimit() + projectListing.getBackEndSizeLimit() != projectListing.getTeamSizeLimit()){
            throw new TeamSizeDiscrepancyError();
        }

        List<String> frontEndMemberList = new ArrayList<>();
        List<String> backEndMemberList = new ArrayList<>();

        if(projectListing.getProjectCreatorPosition().equals(FRONT_END)){
            frontEndMemberList.add(projectListing.getProjectCreator());
        } else if (projectListing.getProjectCreatorPosition().equals(BACK_END)){
            backEndMemberList.add(projectListing.getProjectCreator());
        } else {
            throw new NoTeamChosenError();
        }

        return this.projectListings.save(ProjectListingDocument.builder()
                .projectCreator(projectListing.getProjectCreator())
                .projectName(projectListing.getProjectName())
                .projectDescription(projectListing.getProjectDescription())
                .teamSizeLimit(projectListing.getTeamSizeLimit())
                .frontEndSizeLimit(projectListing.getFrontEndSizeLimit())
                .backEndSizeLimit(projectListing.getBackEndSizeLimit())
                .frontEndMemberList(frontEndMemberList)
                .backEndMemberList(backEndMemberList)
                .build()
        );

    }

    /*
        @ApiResponse(code = 200, message = "ProjectListing was successfully deleted"),
        @ApiResponse(code = 403, message = "User is not Authorized to delete projectListing"),
        @ApiResponse(code = 404, message = "ProjectListing ID does not exist")
     */

    public void deleteListing(String listingID, String uid){
        if(!this.projectListings.existsById(listingID)){ throw new ProjectDoesNotExistError(listingID);}
        if(!confirmUserOwnership(listingID,uid)) { throw new UserNotOwnerOfProjectError(uid, listingID);}
        this.projectListings.deleteById(listingID);
    }

    /*
        @ApiResponse(code = 200, message = "User was successfully added to listing"),
        @ApiResponse(code = 404, message = "Project Listing ID does not exist"),
        @ApiResponse(code = 409, message = "User is already apart of a project"),
        @ApiResponse(code = 400, message = "Team user is attempting to join is full"),
        @ApiResponse(code = 400, message = "Team user is attempting to join does not exist")
     */

    public ProjectListingDocument joinListing(String teamType, String listingID, String uid){

        ProjectListingDocument currentListing = this.projectListings.findById(listingID)
                .orElseThrow(() -> new ProjectDoesNotExistError(listingID));

        if(currentListing.getFrontEndMemberList().contains(uid) || currentListing.getBackEndMemberList().contains(uid)){
            throw new AlreadyHasProjectError();
        }

        switch (teamType){

            case FRONT_END:
                if(currentListing.getFrontEndMemberList().size() >= currentListing.getFrontEndSizeLimit()){
                    throw new TeamFullError(listingID);
                }
                currentListing.getFrontEndMemberList().add(uid);
                break;
            case BACK_END:
                if(currentListing.getBackEndMemberList().size() >= currentListing.getBackEndSizeLimit()){
                    throw new TeamFullError(listingID);
                }
                currentListing.getBackEndMemberList().add(uid);
                break;
            default:
                throw new InvalidTeamTypeError(teamType);

        }

        return projectListings.save(currentListing);
    }

    public ProjectListingDocument removeFromListing(String listingID, String uid){
        ProjectListingDocument currentListing = this.projectListings.findById(listingID)
                                                .orElseThrow(() -> new ProjectDoesNotExistError(listingID));

        List<String> frontEndList = currentListing.getFrontEndMemberList();
        List<String> backEndList = currentListing.getBackEndMemberList();

        if(frontEndList.contains(uid)){
            frontEndList.remove(uid);
        } else if(backEndList.contains(uid)){
            backEndList.remove(uid);
        } else {
            throw new UserNotFoundError(uid);
        }

        return projectListings.save(currentListing);
    }

    public boolean confirmUserOwnership(String projectListingID, String uid){

        ProjectListingDocument currentListing = this.projectListings.findById(projectListingID)
                .orElseThrow(() -> new ProjectDoesNotExistError(projectListingID));

        return currentListing.getProjectCreator().equals(uid);
    }

    public ProjectListingDocument findProject(String projectID){
        return this.projectListings.findById(projectID)
                .orElseThrow(()-> new ProjectDoesNotExistError(projectID));
    }


}
