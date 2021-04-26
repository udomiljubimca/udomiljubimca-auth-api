package com.auth.testlogin.controller;

import com.auth.testlogin.exceptions.WrongCredentialsException;
import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.service.KeyCloakServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.util.Optional;

/**
 * @author Djordje
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/keycloak")
public class KeycloakController {

    @Autowired
    KeyCloakServiceImpl keyClockService;

    /*
     * Get token for the first time when user log in. We need to pass
     * credentials only once. Later communication will be done by sending token.
     */
    // TODO: Important for Exception Handlers !
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @Loggable
    public ResponseEntity<?> getTokenUsingCredentials(@RequestBody UserCredentials userCredentials, ServletRequest request)
            throws Exception {

        if (request == null) {
            // TODO: 18.4.21. Return malformed JSON request
            return ResponseEntity.of(Optional.of(HttpStatus.BAD_REQUEST));
        }

        // TODO: 18.4.21. Credential validation
        if (userCredentials == null || userCredentials.getPassword() == null || userCredentials.getPassword().equals("")
                || userCredentials.getUsername() == null || userCredentials.getUsername().equals("")) {
            throw new WrongCredentialsException("Username or password you applied is not correct. Please try again.");
        }

        TokenDto responseToken;

        responseToken = keyClockService.getToken(userCredentials, request);
        // TODO: 18.4.21. Generic API response
        return new ResponseEntity<>(responseToken, HttpStatus.OK);

    }
    /*
     * When access token get expired than send refresh token to get new access
     * token. We will receive new refresh token also in this response.Update
     * client cookie with updated refresh and access token
     */
    @RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
    @Loggable
    public ResponseEntity<?> getTokenUsingRefreshToken(@RequestHeader(value = "Authorization") String refreshToken) {

        TokenDto responseToken;
        try {
            responseToken = keyClockService.getByRefreshToken(refreshToken);

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(responseToken, HttpStatus.OK);

    }
}
