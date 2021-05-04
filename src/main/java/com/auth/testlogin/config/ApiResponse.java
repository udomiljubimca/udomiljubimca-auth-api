package com.auth.testlogin.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
@Builder
@AllArgsConstructor
@ApiModel(description = "ApiResponse details")
public class ApiResponse {

    /**
     * response status
     */
    @ApiModelProperty(value = "Response status ex: 200, 401, 404")
    private Integer status;

    /**
     * response data
     */
    @ApiModelProperty(value = "Response data")
    private Object data;

    public ApiResponse() {
        this.status = HttpStatus.OK.value();
    }

    public ApiResponse(Object data) {
        this.status = HttpStatus.OK.value();
        this.data = data;
    }


}
