package agileluvr.project;

import agileluvr.common.documents.ProjectListingDocument;
import agileluvr.common.errors.project.*;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.errors.user.UserNotOwnerOfProjectError;
import agileluvr.common.models.ProjectListingModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static agileluvr.common.Identifiers.UserTeamIdentifier.BACK_END;
import static agileluvr.common.Identifiers.UserTeamIdentifier.FRONT_END;
import static agileluvr.common.constraints.TeamSizeContraints.MAXIMUM_TEAM_MEMBERS;
import static agileluvr.common.constraints.TeamSizeContraints.MINIMUM_TEAM_MEMBERS;


@Service
@ApiOperation("Listed Projects API")
public class ProjectListingController {

    private final ProjectListingRepository projectListings;

    public ProjectListingController(ProjectListingRepository projectListings){
        this.projectListings = projectListings;
    }

    public ProjectListingDocument createListing(@RequestBody @ApiParam(name = "Project Listing Format",
                                                        value = "Project Listing to create") ProjectListingModel projectListing){

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
                .hasProjectManager(projectListing.getHasProjectManager())
                .build()
        );

    }

    public void deleteListing(@ApiParam(name = "id of listed project", value = "listed project") String listingID,
                              @ApiParam(name = "id", value = "user id") String uid){

        if(!this.projectListings.existsById(listingID)){ throw new ProjectDoesNotExistError(listingID);}

        if(!confirmUserOwnership(listingID,uid)) { throw new UserNotOwnerOfProjectError(uid, listingID);}

        this.projectListings.deleteById(listingID);
    }

    public ProjectListingDocument joinListing(@ApiParam(name = "team user will join", value = "team choice") String teamType,
                                              @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                              @ApiParam(name = "user id", value = "user trying to join project") String uid){

        ProjectListingDocument currentListing = this.projectListings.findById(listingID)
                .orElseThrow(() -> new ProjectDoesNotExistError(listingID));

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

    public boolean confirmUserOwnership(@ApiParam(name ="Project Listing id", value = "Project Listing") String projectListingID,
                                        @ApiParam(name = "id", value = "user id") String uid){

        ProjectListingDocument currentListing = this.projectListings.findById(projectListingID)
                .orElseThrow(() -> new ProjectDoesNotExistError(projectListingID));

        return currentListing.getProjectCreator().equals(uid);
    }


}
