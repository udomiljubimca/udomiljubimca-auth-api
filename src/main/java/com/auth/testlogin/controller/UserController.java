package com.auth.testlogin.controller;

import com.auth.testlogin.logging.Loggable;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.service.KeyCloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import java.util.Optional;

/**
 * @author Djordje
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

	// TODO: 26.4.21. Catch Errors on logout and update/password

	@Autowired
	KeyCloakService keyCloakService;

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@Loggable
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {

		if (request == null) {
			return ResponseEntity.of(Optional.of(HttpStatus.BAD_REQUEST));
		}

		String header = request.getHeader("Authorization");

		keyCloakService.logoutUser(header);

		return new ResponseEntity<>("Hi!, you have logged out successfully!", HttpStatus.OK);

	}

	@RequestMapping(value = "/update/password", method = RequestMethod.GET)
	@Loggable
	public ResponseEntity<?> updatePassword(HttpServletRequest request,
											@RequestParam(name = "userId") String userId,
											@RequestBody ResetPasswordDto resetPasswordDto) {

		if (request == null) {
			return ResponseEntity.of(Optional.of(HttpStatus.BAD_REQUEST));
		}
		if (userId == null) {
			return ResponseEntity.of(Optional.of(HttpStatus.BAD_REQUEST));
		}
		if (resetPasswordDto == null || (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirm()))) {
			return ResponseEntity.of(Optional.of(HttpStatus.BAD_REQUEST));
		}

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			throw new NotAuthorizedException("No JWT token found in request headers");
		}
		String authToken = header.substring(7);

		keyCloakService.resetPassword(resetPasswordDto,authToken, userId);

		return new ResponseEntity<>("Your password has been successfully updated!", HttpStatus.OK);

	}

}
