package com.auth.testlogin.controller;

import com.auth.testlogin.config.ApiResponse;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.service.KeyCloakService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * User controller for Adopt a pet project
 * Acceptance criteria:
 * 1)user logout
 * 2)user update password
 */
@RestController
@RequestMapping(value = "/api/latest/auth-api/user")
public class UserController {

    @Autowired
    KeyCloakService keyCloakService;

    /**
     * 1) Logout route logs out user from application.
     * Requires JWT token in header.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(
            notes = "${operation3.description}",
            value = "${operation3.value}"
    )
    @Loggable
    public ApiResponse logoutUser(HttpServletRequest request) {

        if (request == null) {
            throw new WrongUserCredentialsException("Bad request!");
        }

        String header = request.getHeader("Authorization");

        if (header == null) {
            throw new TokenNotValidException("Refresh token not provided!");
        }

        keyCloakService.logoutUser(header);

        return new ApiResponse("Hi!, you have logged out successfully!");

    }

    /**
     * 2) Update password route updates existing password for user.
     * Requires userId, new password, confirm password
     */
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    @ApiOperation(
            notes = "${operation4.description}",
            value = "${operation4.value}"
    )
    @Loggable
    public ApiResponse updatePassword(@ApiParam(value = "UserId", required = true)
                                      @RequestParam(name = "userId") String userId,
                                      @RequestBody ResetPasswordDto resetPasswordDto) {

        if (userId == null) {
            throw new WrongUserCredentialsException("UserId is not present!");
        }
        if (resetPasswordDto == null) {
            throw new WrongUserCredentialsException("Body is not present!");
        }
        if (resetPasswordDto.getPassword().equalsIgnoreCase("")
                || resetPasswordDto.getConfirm().equalsIgnoreCase("")) {

            throw new WrongUserCredentialsException("Password cannot be empty!");
        }
        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirm())) {
            throw new WrongUserCredentialsException("Provided passwords are not the same!");
        }

        keyCloakService.resetPasswordFromAdmin(resetPasswordDto.getPassword(), userId);

        return new ApiResponse("Your password has been successfully updated!");

    }

}
