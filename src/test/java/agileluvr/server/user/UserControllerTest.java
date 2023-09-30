package agileluvr.server.user;


import agileluvr.Server;
import agileluvr.common.documents.UserDocument;
import agileluvr.common.models.BasicUserModel;
import agileluvr.common.models.SecureUserModel;
import agileluvr.project.ProjectListingRepository;
import agileluvr.project.ProjectRepository;
import agileluvr.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Server.class
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties"
)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private final String API_KEY;

    public UserControllerTest() { this.API_KEY = Dotenv.load().get("AGILELUVR_API_SECRET"); }


    @After
    public void resetDb(){
        userRepository.deleteAll();
    }

    @Test
    public void createUserTest() throws Exception {

        String username = "fourleaf";
        String password = "AmazonPleaseHireMe";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();


        MvcResult result = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        List<UserDocument> foundUsers = userRepository.findAll();

        assertThat(foundUsers).extracting(UserDocument::getUsername).containsOnly(username);

    }

    @Test
    public void createUserNoUsernameTest() throws Exception {

        String username = "";
        String password = "AmazonPLEASEHireMe";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();


        MvcResult result = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
    }

    @Test
    public void createUserNoPasswordTest() throws Exception {

        String username = "fourleaf";
        String password = "";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();


        MvcResult result = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);

    }

    @Test
    public void createUserInvalidKeyTest() throws Exception {
        String username = "fourleaf";
        String password = "";
        String fake_apiKey = "thisKeyDoesntWork";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(fake_apiKey)
                .build();

        MvcResult result = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);

    }


    @Test
    public void getUserThatExists() throws Exception {
        String username = "kevin";
        String password = "AmazonPleaseHireMe";

        SecureUserModel user = SecureUserModel.builder()
                                    .username(username)
                                    .password(password)
                                    .apiPassword(this.API_KEY)
                                    .build();


        MvcResult postUser = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(postUser.getResponse().getStatus()).isEqualTo(200);


        BasicUserModel userToRetrieve = BasicUserModel.builder()
                            .username(username)
                            .password(password)
                            .build();

        ResultActions retrieveUser = mvc.perform(get("/api/user/sign_in")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(mapper.writeValueAsString(userToRetrieve))
        );

        var retrieveUserStatus = retrieveUser.andReturn().getResponse();

        assertThat(retrieveUserStatus.getStatus()).isEqualTo(200);
    }

    @Test
    public void getUserThatDoesNotExist() throws Exception {
        String username = "kevin";
        String password = "AmazonPleaseHireMe";

        String fakeUsername = "IDoNotExist";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();


        MvcResult postUser = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(postUser.getResponse().getStatus()).isEqualTo(200);

        BasicUserModel userToRetrieve = BasicUserModel.builder()
                .username(fakeUsername)
                .password(password)
                .build();

        ResultActions retrieveUser = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userToRetrieve))
        );

        var retrieveUserStatus = retrieveUser.andReturn().getResponse();

        assertThat(retrieveUserStatus.getStatus()).isEqualTo(404);

    }

    @Test
    public void getUserWithWrongPassword () throws Exception {
        String username = "kevin";
        String password = "AmazonPleaseHireMe";

        String incorrectPassword = "THISPASSWORDISCORRECTISWEAR";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();


        MvcResult postUser = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(postUser.getResponse().getStatus()).isEqualTo(200);

        BasicUserModel userToRetrieve = BasicUserModel.builder()
                .username(username)
                .password(incorrectPassword )
                .build();

        ResultActions retrieveUser = mvc.perform(get("/api/user/sign_in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userToRetrieve))
        );

        var retrieveUserStatus = retrieveUser.andReturn().getResponse();

        assertThat(retrieveUserStatus.getStatus()).isEqualTo(404);
    }

    @Test
    public void changeListingOfExistingUser() throws Exception {
        String username = "kevin";
        String password = "AmazonPleaseHireMe";

        String projectID = "123";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        MvcResult postedUser = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(postedUser.getResponse().getStatus()).isEqualTo(200);

        String userID = mapper.readTree(postedUser.getResponse().getContentAsString()).get("id").asText();

        MvcResult changedListing = mvc.perform(put("/api/user/" + projectID + "/" +  userID + "/change"))
                                        .andReturn();

        assertThat(changedListing.getResponse().getStatus()).isEqualTo(200);

        String retrievedProjectID = mapper.readTree(changedListing.getResponse().getContentAsString()).get("activeProjectID").asText();

        assertThat(retrievedProjectID.equals(projectID));

    }

    @Test
    public void changeListingOfNonExistingUser() throws Exception{
        String projectID = "123";
        String userID = "randomID";

        MvcResult changedListing = mvc.perform(put("/api/user/" + projectID + "/" +  userID + "/change"))
                .andReturn();

        assertThat(changedListing.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    public void addListingToExistingUser() throws Exception {
        String username = "kevin";
        String password = "AmazonPleaseHireMe";

        String projectID = "123";

        SecureUserModel user = SecureUserModel.builder()
                .username(username)
                .password(password)
                .apiPassword(this.API_KEY)
                .build();

        MvcResult postedUser = mvc.perform(post("/api/user/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                        .andReturn();

        assertThat(postedUser.getResponse().getStatus()).isEqualTo(200);

        String userID = mapper.readTree(postedUser.getResponse().getContentAsString()).get("id").asText();

        MvcResult addedListing = mvc.perform(put("/api/user/"+projectID+"/"+userID+"/complete"))
                                            .andReturn();

        assertThat(addedListing.getResponse().getStatus()).isEqualTo(200);

        String postedUsersCompletedProjects = mapper.readTree(addedListing.getResponse().getContentAsString()).get("previousProjects").asText();

        assertThat(postedUsersCompletedProjects.equals(projectID));

    }

    @Test
    public void addListingToNonExistingUser() throws Exception {
        String userID = "thisDoesntExistInsideTheDataBase";
        String projectID = "123";
        MvcResult addedListing = mvc.perform(put("/api/user/"+projectID+"/"+userID+"/complete"))
                .andReturn();

        assertThat(addedListing.getResponse().getStatus()).isEqualTo(404);
    }




}
