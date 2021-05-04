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
public class TokenDto {

    /**
     * access token
     */
    @ApiModelProperty(value = "${TokenDto.accessToken}")
    private String accessToken;

    /**
     * token type
     */
    @ApiModelProperty(value = "${TokenDto.tokenType}")
    private String tokenType;

    /**
     * refresh token
     */
    @ApiModelProperty(value = "${TokenDto.refreshToken}")
    private String refreshToken;

    /**
     * token expiration
     */
    @ApiModelProperty(value = "${TokenDto.expires_in}")
    private String expires_in;

    /**
     * scope
     */
    @ApiModelProperty(value = "${TokenDto.scope}")
    private String scope;

    /**
     * user info object
     */
    @ApiModelProperty(value = "${TokenDto.userInfo}")
    private UserInfoDto userInfo;
}
