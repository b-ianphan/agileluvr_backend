package agileluvr.user;


import agileluvr.common.documents.UserDocument;
import agileluvr.common.models.BasicUserModel;
import agileluvr.common.models.ReducedUserModel;
import agileluvr.common.models.SecureUserModel;
import agileluvr.common.errors.user.NotAuthorizedError;
import agileluvr.common.errors.user.UserNotFoundError;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static agileluvr.common.Identifiers.ProjectIdentifier.NO_PROJECT_ASSIGNED;


@RestController
@RequestMapping("/api/user")
@ApiOperation("Users API")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService){ this.userService = userService;}

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/sign_up")
    public ReducedUserModel createUser(@RequestBody SecureUserModel user){
        return this.userService.createUser(user);
    }

    @ApiOperation(value = "Sign in", notes = "Get the User ID with a username and password")
    @GetMapping("/sign_in")
    public ReducedUserModel getUser(@RequestBody BasicUserModel user){
        return this.userService.getUser(user);
    }


    @ApiOperation(value = "Assign or Unassign", notes = "Add or remove user to/from project listing")
    @PutMapping("/{listingID}/{uid}/leave")
    public UserDocument changeListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                      @PathVariable @ApiParam(name = "id of user", value = "user") String uid){

        return this.userService.changeListing(listingID, uid);

    }


    @PutMapping("/{projectID}/{uid}/complete")
    public UserDocument addCompletedProject(@PathVariable @ApiParam(name = "id of project", value = "completed project") String projectID,
                                            @PathVariable @ApiParam(name = "id of user", value = "user") String uid){

        return this.userService.addCompletedProject(projectID, uid);
    }



}
