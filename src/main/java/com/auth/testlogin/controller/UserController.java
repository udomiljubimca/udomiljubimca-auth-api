package com.auth.testlogin.controller;

import com.auth.testlogin.service.KeyCloakService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;

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
	public ResponseEntity<?> logoutUser(HttpServletRequest request) {

		request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		AccessToken token = ((KeycloakPrincipal<?>) request.getUserPrincipal()).getKeycloakSecurityContext().getToken();

		String userId = token.getSubject();

		keyCloakService.logoutUser(userId);

		return new ResponseEntity<>("Hi!, you have logged out successfully!", HttpStatus.OK);

	}

	@RequestMapping(value = "/update/password", method = RequestMethod.GET)
	public ResponseEntity<?> updatePassword(HttpServletRequest request,
											@RequestParam(name = "userId") String userId,
											@RequestBody CredentialRepresentation credentialRepresentation) {

		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Bearer ")) {
			throw new NotAuthorizedException("No JWT token found in request headers");
		}
		String authToken = header.substring(7);

		keyCloakService.resetPassword(credentialRepresentation,authToken, userId);

		return new ResponseEntity<>("Your password has been successfully updated!", HttpStatus.OK);

	}

}
