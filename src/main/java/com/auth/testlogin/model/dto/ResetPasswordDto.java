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
@ApiModel(description = "${ResetPasswordDto.description}")
public class ResetPasswordDto {

    /**
     * new password
     */
    @ApiModelProperty(value="${ResetPasswordDto.password}")
    private String password;

    /**
     * password confirm
     */
    @ApiModelProperty(value="${ResetPasswordDto.confirm}")
    private String confirm;
}
