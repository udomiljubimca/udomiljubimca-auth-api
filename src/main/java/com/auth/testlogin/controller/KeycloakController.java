package com.auth.testlogin.controller;

import com.auth.testlogin.config.ApiResponse;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Djordje
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/keycloak")
public class KeycloakController {

    @Autowired
    KeyCloakService keyClockService;

    /*
     * Get token for the first time when user log in. We need to pass
     * credentials only once. Later communication will be done by sending token.
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @Loggable
    public ApiResponse getTokenUsingCredentials(@RequestBody UserCredentials userCredentials, ServletRequest request) {

        ApiResponse apiResponse = new ApiResponse();
        TokenDto responseToken;

        if (request == null) {
            throw new WrongUserCredentialsException("Bad request!");
        }
        if (userCredentials == null){
            throw new WrongUserCredentialsException("Bad request!");
        }

        responseToken = keyClockService.getToken(userCredentials, request);

        apiResponse.setData(responseToken);

        return apiResponse;

    }
    /*
     * When access token get expired than send refresh token to get new access
     * token. We will receive new refresh token also in this response.Update
     * client cookie with updated refresh and access token
     */
    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    @Loggable
    public ResponseEntity<?> getTokenUsingRefreshToken(HttpServletRequest request) {

        TokenDto responseToken;

        String header = request.getHeader("Authorization");
        if(header == null){
            throw new TokenNotValidException("Refresh token is not present!");
        }

        responseToken = keyClockService.getByRefreshToken(header);

        return new ResponseEntity<>(responseToken, HttpStatus.OK);


    }

}
