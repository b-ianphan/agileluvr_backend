package agileluvr.common.documents;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;


@Document("agile-projects")
@Data
@SuperBuilder
public class ProjectDocument {

    @ApiModelProperty(notes = "Unique ID generated by MongoDB")
    @Id
    private String id;

    @ApiModelProperty(notes = "ID of user who created the project", required = true)
    @NotBlank
    private String projectCreator;

    @ApiModelProperty(notes = "Name of project", required = true)
    @Indexed(name = "Project Name", unique = true)
    @NotBlank
    private String projectName;

    @ApiModelProperty(notes = "Description of project", required = true)
    @NotBlank
    private String projectDescription;

    @ApiModelProperty(notes = "IDs of members working on front end of this project", required = true)
    private List<String> frontEndMemberList;

    @ApiModelProperty(notes = "IDs of members working on back end of this project", required = true)
    private List<String> backEndMemberList;

    @ApiModelProperty(notes = "ID of Project Manager")
    private String projectManager;

    @ApiModelProperty(notes = "Project can either be in progress or completed")
    private boolean inProgress;

}
