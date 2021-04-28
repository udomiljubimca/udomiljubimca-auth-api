package com.auth.testlogin.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class ApiException {

    private String timeStamp;
    private String message;
    private String details;

    public ApiException(Date timeStamp, String message, String details) {
        this.timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timeStamp);;
        this.message = message;
        this.details = details;
    }
}
