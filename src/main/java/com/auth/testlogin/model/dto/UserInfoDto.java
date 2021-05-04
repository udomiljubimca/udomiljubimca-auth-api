package com.auth.testlogin.model.dto;

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
@ApiModel(description = "${TokenDto.description}")
public class UserInfoDto {

    /**
     * unique user number
     */
    @ApiModelProperty(value = "${UserInfoDto.sub}")
    String sub;

    /**
     * user verified email
     */
    @ApiModelProperty(value = "${UserInfoDto.email_verified}")
    Boolean email_verified;

    /**
     * user username
     */
    @ApiModelProperty(value = "${UserInfoDto.preferred_username}")
    String preferred_username;

    /**
     * user email
     */
    @ApiModelProperty(value = "${UserInfoDto.preferred_username}")
    String email;

    /**
     * user full name
     */
    @ApiModelProperty(value = "${UserInfoDto.preferred_username}")
    String name;

}
