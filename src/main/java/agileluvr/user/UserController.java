package agileluvr.user;


import agileluvr.common.documents.UserDocument;
import agileluvr.common.models.BasicUserModel;
import agileluvr.common.models.ReducedUserModel;
import agileluvr.common.models.SecureUserModel;
import agileluvr.common.errors.NotAuthorizedError;
import agileluvr.common.errors.UserNotFoundError;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user")
@ApiOperation("Users API")
public class UserController {
    private final UserRepository users;

    @Value("${agileluvr.API_SECRET}")
    private String apiPassword;

    public UserController(UserRepository users){ this.users = users;}

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

}
