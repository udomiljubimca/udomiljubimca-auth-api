package com.auth.testlogin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "${UserCredentials.description}")
public class UserCredentials {

    /**
     * user password
     */
    @ApiModelProperty(value = "${UserCredentials.password}")
    private String password;

    /**
     * user username
     */
    @ApiModelProperty(value = "${UserCredentials.username}")
    private String username;

}
