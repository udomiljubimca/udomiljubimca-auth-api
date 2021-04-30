package com.auth.testlogin.controller;

import com.auth.testlogin.config.ApiResponse;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Keycloak controller for Adopt a pet project
 * Acceptance criterias:
 * 1)get token for the first time when user log in
 * 2)get new access token.
 */
@RestController
@RequestMapping(value = "/")
public class KeycloakController {

    @Autowired
    KeyCloakService keyClockService;

    /**
     * 1) Get token for the first time when user log in. We need to pass
     * credentials only once. Later communication will be done by sending token.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(
            notes="${operation1.description}",
            value="${operation1.value}",
            responseContainer="${operation1.responseContainer}",
            response = TokenDto.class
    )
    @Loggable
    public ApiResponse getTokenUsingCredentials(@RequestBody UserCredentials userCredentials, ServletRequest request) {

        TokenDto responseToken;

        if (request == null) {
            return new ApiResponse(new WrongUserCredentialsException("Bad request!"));
        }
        if (userCredentials == null){
            throw new WrongUserCredentialsException("Bad request!");
        }

        responseToken = keyClockService.getToken(userCredentials, request);

        return new ApiResponse(responseToken);

    }
    /**
     * 2) When access token get expired than send refresh token to get new access
     * token. We will receive new refresh token also in this response.Update
     * client cookie with updated refresh and access token
     */
    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    @ApiOperation(
            notes="${operation2.description}",
            value="${operation2.value}",
            responseContainer="${operation2.responseContainer}",
            response = TokenDto.class
    )
    @Loggable
    public ApiResponse getTokenUsingRefreshToken(HttpServletRequest request) {

        TokenDto responseToken;

        String header = request.getHeader("Authorization");
        if(header == null){
            throw new TokenNotValidException("Refresh token is not present!");
        }

        responseToken = keyClockService.getByRefreshToken(header);

        return new ApiResponse(responseToken);

    }

}
