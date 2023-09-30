package agileluvr.server.user;

import agileluvr.Server;
import agileluvr.common.documents.UserDocument;
import agileluvr.common.errors.user.UserNotFoundError;
import agileluvr.common.models.BasicUserModel;
import agileluvr.common.models.ReducedUserModel;
import agileluvr.common.models.SecureUserModel;
import agileluvr.user.UserRepository;
import agileluvr.user.UserService;
import com.mongodb.DuplicateKeyException;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.catalina.User;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Server.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties"
)
public class UserServiceTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final String API_KEY;

    public UserServiceTest(){
        this.API_KEY = Dotenv.load().get("AGILELUVR_API_SECRET");
    }

    @After
    public void resetDb(){
        this.userRepository.deleteAll();
    }

    @Test
    public void createValidUser() throws Exception {

        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);

        List<UserDocument> foundUsers = userRepository.findAll();
        assertThat(foundUsers).extracting(UserDocument::getUsername).containsOnly(username);
    }

    @Test(expected = ConstraintViolationException.class)
    public void createUserInvalidPassword() {
        String username = "fourleaf";
        String password = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);
    }

    @Test(expected = ConstraintViolationException.class)
    public void createUserInvalidUsername(){
        String username = "";
        String password = "AmazonPleaseHireMe";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);
    }

    @Test(expected = ConstraintViolationException.class)
    public void createUserInvalidUsernameAndPassword(){
        String username = "";
        String password = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);
    }

// lets handle this in userController
//    @Test
//    public void createUserDuplicateUsernameTest(){
//        String username1 = "fourleaf";
//        String username2 = "fourleaf";
//
//        String password = "AmazonPleaseIWasntJoking";
//
//        SecureUserModel user1 = SecureUserModel.builder()
//                .username(username1)
//                .password(password)
//                .apiPassword(this.API_KEY)
//                .build();
//
//        userService.createUser(user1);
//
//
//        SecureUserModel user2 = SecureUserModel.builder()
//                .username(username2)
//                .password(password)
//                .apiPassword(this.API_KEY)
//                .build();
//
//
//        userService.createUser(user2);
//
//        List<UserDocument> foundUsers = userRepository.findAll();
//        assertThat(foundUsers).extracting(UserDocument::getUsername).containsExactly(username1);
//    }

    @Test
    public void getValidUser() {
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        ReducedUserModel createdUser = userService.createUser(user);

        BasicUserModel userToRetrieve = BasicUserModel.builder()
                                            .username(username)
                                            .password(password)
                                            .build();

        ReducedUserModel retrievedUser = userService.getUser(userToRetrieve);

        assertThat(retrievedUser.getId().equals(createdUser.getId()));
    }

    @Test(expected = UserNotFoundError.class)
    public void getUserWithWrongUsername(){
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";

        String falseUser = "threeleaf";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);

        BasicUserModel userToRetrieve = BasicUserModel.builder()
                .username(falseUser)
                .password(password)
                .build();

        userService.getUser(userToRetrieve);
    }

    @Test(expected = UserNotFoundError.class)
    public void getUserWithWrongPassword(){
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";

        String falsePassword = "HahaJokesOnYou";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        userService.createUser(user);

        BasicUserModel userToRetrieve = BasicUserModel.builder()
                .username(username)
                .password(falsePassword)
                .build();

        userService.getUser(userToRetrieve);
    }

    @Test
    public void setUserListing(){
        String someProjectID = "someProjectID";
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";
        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        ReducedUserModel createdUser = userService.createUser(user);

        userService.changeListing(someProjectID, createdUser.getId());

        List<UserDocument> foundUsers = userRepository.findAll();

        assertThat(foundUsers).extracting(UserDocument::getActiveProjectID).containsExactly("someProjectID");
    }

    @Test(expected = UserNotFoundError.class)
    public void setUserListingInvalidUser(){
        String someProjectID = "someProjectID";
        userService.changeListing(someProjectID, "fakeID");
    }


    @Test
    public void addCompletedProjectValidUser(){
        String someProjectID = "someProjectID";
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";
        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        ReducedUserModel createdUser = userService.createUser(user);

        userService.addCompletedProject(someProjectID, createdUser.getId());

        List<UserDocument> foundUsers = userRepository.findAll();

        List<String> expectedProject = new ArrayList<>();
        expectedProject.add(someProjectID);

        assertThat(foundUsers).extracting(UserDocument::getPreviousProjects).containsExactly(expectedProject);
    }

    @Test(expected = UserNotFoundError.class)
    public void addCompletedProjectInValidUser(){
        String someProjectID = "someProjectID";
        String username_noUserRelation = "blahblahblah";
        userService.addCompletedProject(someProjectID,  username_noUserRelation);
    }

    @Test
    public void userHasActiveProject(){
        String someProjectID = "someProjectID";
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";
        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        ReducedUserModel createdUser = userService.createUser(user);

        userService.changeListing(someProjectID, createdUser.getId());

        assertThat(userService.hasActiveProject(createdUser.getId()) == true);
    }

    @Test
    public void userHasNoActiveProject(){
        String someProjectID = "someProjectID";
        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";
        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        ReducedUserModel createdUser = userService.createUser(user);
        assertThat(userService.hasActiveProject(createdUser.getId()) == false);
    }


}
