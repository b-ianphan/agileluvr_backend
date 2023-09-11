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
    private final UserRepository users;

    @Value("${agileluvr.API_SECRET}")
    private String apiPassword;

    public UserController(UserRepository users){ this.users = users;}

    @ApiOperation(value = "Sign up", notes = "Sign up with a username and password")
    @PostMapping("/sign_up")
    public ReducedUserModel createUser(@RequestBody SecureUserModel user){
        if (user.getApiPassword() != null && user.getApiPassword().equals(this.apiPassword)) {
            UserDocument savedUser = users.saveUsernameAndPassword(user.getUsername(), user.getPassword());
            return ReducedUserModel.builder()
                    .username(savedUser.getUsername())
                    .id(savedUser.getId())
                    .build();
        } else throw new NotAuthorizedError();
    }

    @ApiOperation(value = "Sign in", notes = "Get the User ID with a username and password")
    @GetMapping("/sign_in")
    public ReducedUserModel getUser(@RequestBody BasicUserModel user){
        UserDocument foundUser = users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));

        return ReducedUserModel.builder()
                .username(foundUser.getUsername())
                .id(foundUser.getId())
                .build();
    }


    @ApiOperation(value = "Assign or Unassign", notes = "Add or remove user to/from project listing")
    @PutMapping("/{listingID}/{uid}/leave")
    public UserDocument changeListing(@PathVariable @ApiParam(name = "id of listed project", value = "listed project") String listingID,
                                      @PathVariable @ApiParam(name = "id of user", value = "user") String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        foundUser.setActiveProjectID(listingID);

        return users.save(foundUser);


    }


    @PutMapping("/{projectID}/{uid}/complete")
    public UserDocument addCompletedProject(@PathVariable @ApiParam(name = "id of project", value = "completed project") String projectID,
                                            @PathVariable @ApiParam(name = "id of user", value = "user") String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        foundUser.getPreviousProjects().add(projectID);

        return users.save(foundUser);
    }

    public boolean hasActiveProject(String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        return !foundUser.getActiveProjectID().equals(NO_PROJECT_ASSIGNED);

    }



}
