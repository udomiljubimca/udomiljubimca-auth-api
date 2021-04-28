package com.auth.testlogin.controller;

import com.auth.testlogin.config.ApiResponse;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.service.KeyCloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Djordje
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    KeyCloakService keyCloakService;

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @Loggable
    public ApiResponse logoutUser(HttpServletRequest request) {

        if (request == null) {
            throw new WrongUserCredentialsException("Bad request!");
        }

        String header = request.getHeader("Authorization");

        keyCloakService.logoutUser(header);

        return new ApiResponse("Hi!, you have logged out successfully!");

    }

    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    @Loggable
    public ApiResponse updatePassword(HttpServletRequest request,
                                      @RequestParam(name = "userId") String userId,
                                      @RequestBody ResetPasswordDto resetPasswordDto) {

        if (request == null) {
            throw new WrongUserCredentialsException("Bad request!");
        }
        if (userId == null) {
            throw new WrongUserCredentialsException("User Id not present!");
        }
        if (resetPasswordDto == null){
            throw new WrongUserCredentialsException("Body not present!");
        }
        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirm())) {
            throw new WrongUserCredentialsException("Password are not the same!");
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new WrongUserCredentialsException("No JWT token found in request headers");
        }
        String authToken = header.substring(7);

        keyCloakService.resetPassword(resetPasswordDto, authToken, userId);

        return new ApiResponse("Your password has been successfully updated!");

    }

}
