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
public class SecureUserModel {
    @ApiModelProperty(notes = "Username of the user", required = true)
    private String username;

    @ApiModelProperty(notes = "Password of the user", required = true)
    private String password;

    @ApiModelProperty(notes = "The secret required to use this API", required = true)
    private String apiPassword;
}