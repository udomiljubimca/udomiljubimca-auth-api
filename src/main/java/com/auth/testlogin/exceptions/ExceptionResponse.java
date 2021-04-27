package com.auth.testlogin.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

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
