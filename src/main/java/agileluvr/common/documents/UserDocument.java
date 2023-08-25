package agileluvr.common.documents;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;


@Document("workout-users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserDocument {

    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "Username of the user", required = true)
    @Indexed(name = "username", unique = true)
    @NotEmpty
    private String username;

    @ApiModelProperty(notes = "Password of the user", required = true)
    @NotEmpty
    private String password;

}
