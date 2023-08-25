package agileluvr.common.models;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserModel {
    @ApiModelProperty(notes = "Username of the user", required = true)
    private String username;

    @ApiModelProperty(notes = "Password of the user", required = true)
    private String password;
}
