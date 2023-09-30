package agileluvr.user;

import agileluvr.common.documents.UserDocument;
import agileluvr.common.errors.user.NotAuthorizedError;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.models.BasicUserModel;
import agileluvr.common.models.ReducedUserModel;
import agileluvr.common.models.SecureUserModel;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import static agileluvr.common.Identifiers.ProjectIdentifier.NO_PROJECT_ASSIGNED;

@Service
public class UserService {

    private final UserRepository users;

    //@Value("${agileluvr.API_SECRET}")
    // idk why this gives me an error
    private final String apiPassword = Dotenv.load().get("AGILELUVR_API_SECRET");;

    @Autowired
    public UserService(UserRepository users){
        this.users = users;
    }

    public ReducedUserModel createUser(SecureUserModel user){

        if (user.getApiPassword() != null && user.getApiPassword().equals(this.apiPassword)) {
            UserDocument savedUser = users.saveUsernameAndPassword(user.getUsername(), user.getPassword());
            return ReducedUserModel.builder()
                    .username(savedUser.getUsername())
                    .id(savedUser.getId())
                    .build();
        } else throw new NotAuthorizedError();
    }

    public ReducedUserModel getUser(BasicUserModel user){
        UserDocument foundUser = users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));

        return ReducedUserModel.builder()
                .username(foundUser.getUsername())
                .id(foundUser.getId())
                .build();
    }

    public UserDocument changeListing(String listingID, String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        foundUser.setActiveProjectID(listingID);

        return users.save(foundUser);
    }

    public UserDocument addCompletedProject(String projectID, String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        foundUser.getPreviousProjects().add(projectID);

        return users.save(foundUser);
    }

    public boolean hasActiveProject(String uid){

        UserDocument foundUser = users.findById(uid)
                .orElseThrow(() -> new UserNotFoundError(uid));

        if(foundUser.getActiveProjectID() == null){
            return false;
        }

        return !foundUser.getActiveProjectID().equals(NO_PROJECT_ASSIGNED);

    }

    public boolean exists(String uid){
        return this.users.existsById(uid);
    }
}
