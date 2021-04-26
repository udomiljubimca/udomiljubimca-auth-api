package com.auth.testlogin.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ExceptionResponse {
    private Date timeStamp;
    private String message;
    private String details;

}
