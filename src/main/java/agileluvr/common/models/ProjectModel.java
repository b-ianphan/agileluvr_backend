package agileluvr.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ProjectModel {

    @ApiModelProperty(notes = "ID of user who created the project", required = true)

    private String projectCreator;

    @ApiModelProperty(notes = "Name of project", required = true)
    private String projectName;

    @ApiModelProperty(notes = "Description of project", required = true)

    private String projectDescription;

    @ApiModelProperty(notes = "IDs of members working on front end of this project", required = true)
    private List<String> frontEndMemberList;

    @ApiModelProperty(notes = "IDs of members working on back end of this project", required = true)
    private List<String> backEndMemberList;

    @ApiModelProperty(notes = "ID of Project Manager, null if they dont exists ")
    private String projectManager;


}
