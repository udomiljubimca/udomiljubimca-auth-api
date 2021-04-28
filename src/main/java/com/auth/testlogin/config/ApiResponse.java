package com.auth.testlogin.config;

import lombok.*;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@ToString
@Getter
@Setter
public class ApiResponse {

    private Integer status;
    private Object data;
    private Object error;

    public ApiResponse() {
        this.status = HttpStatus.OK.value();
    }

    public ApiResponse(Object data) {
        this.status = HttpStatus.OK.value();
        this.data = data;
    }


}
