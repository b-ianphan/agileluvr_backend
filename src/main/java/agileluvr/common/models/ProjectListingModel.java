package agileluvr.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListingModel {

    @ApiModelProperty(notes = "ID of user who created the project")
    private String projectCreator;


    @ApiModelProperty(notes = "Position original creator will be")
    private String projectCreatorPosition;

    @ApiModelProperty(notes = "Name of project")
    private String projectName;

    @ApiModelProperty(notes = "Description of project")
    private String projectDescription;

    @ApiModelProperty(notes = "Team size limit of project, max 6 programmers")
    private int teamSizeLimit;

    @ApiModelProperty(notes = "Limit of front end team size")
    private int frontEndSizeLimit;

    @ApiModelProperty(notes = "Limit of back end team size")
    private int backEndSizeLimit;
}
