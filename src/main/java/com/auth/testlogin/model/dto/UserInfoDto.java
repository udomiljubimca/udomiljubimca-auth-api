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
    @ApiModelProperty(value="${TokenDto.sub}")
    String sub;

    /**
     * user verified email
     */
    @ApiModelProperty(value="${TokenDto.email_verified}")
    Boolean email_verified;

    /**
     * user username
     */
    @ApiModelProperty(value="${TokenDto.email_verified}")
    String preferred_username;

}
