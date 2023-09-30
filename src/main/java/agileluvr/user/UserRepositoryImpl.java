package agileluvr.user;

import agileluvr.common.documents.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Optional;

import static agileluvr.common.Identifiers.ProjectIdentifier.NO_PROJECT_ASSIGNED;

interface UserRepositoryInterface {
    Optional<UserDocument> findByUsernameAndPassword(String username, String password);

    UserDocument saveUsernameAndPassword(String username, String password);
}

public class UserRepositoryImpl implements UserRepositoryInterface {

    @Autowired
    private MongoTemplate template;
    private final BCryptPasswordEncoder encoder;

    public UserRepositoryImpl() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public Optional<UserDocument> findByUsernameAndPassword(String username, String password) {
        Query userQuery = new Query(Criteria.where("username").is(username));
        UserDocument user = template.findOne(userQuery, UserDocument.class);

        return user != null && encoder.matches(password, user.getPassword()) ? Optional.of(user) : Optional.empty();
    }

    @Override
    public UserDocument saveUsernameAndPassword(String username, String password) {
        if (password == null || password.isEmpty()) {
            throw new ConstraintViolationException("Password cannot be empty", null);
        }

        if(username == null || username.isEmpty()) {
            throw new ConstraintViolationException("Username cannot be empty", null);
        }

        String hashedPassword = encoder.encode(password);
        UserDocument user = UserDocument.builder()
                .username(username)
                .password(hashedPassword)
                .activeProjectID(NO_PROJECT_ASSIGNED)
                .previousProjects(new ArrayList<>())
                .build();

        return template.save(user);
    }
}
