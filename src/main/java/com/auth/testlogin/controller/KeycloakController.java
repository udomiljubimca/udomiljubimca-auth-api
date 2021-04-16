package com.auth.testlogin.controller;

import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.UserInfoDto;
import com.auth.testlogin.service.KeyCloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import javax.servlet.ServletRequest;

@RestController
@RequestMapping(value = "/keycloak")
public class KeycloakController {

	@Autowired
	KeyCloakService keyClockService;

	/*
	 * Get token for the first time when user log in. We need to pass
	 * credentials only once. Later communication will be done by sending token.
	 */
	// TODO: Important for Exception Handlers !
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> getTokenUsingCredentials(@RequestBody UserCredentials userCredentials, ServletRequest request) throws Exception {

		TokenDto responseToken;
		if(userCredentials != null) {
			responseToken = keyClockService.getToken(userCredentials, request);
		}else{
			throw new CredentialException("Wrong username or password!");
		}

		return new ResponseEntity<>(responseToken, HttpStatus.OK);

	}
	@GetMapping(value = "/userInfo/{token}")
	public ResponseEntity<?> getUserInfo(@PathVariable String token){

		UserInfoDto userInfoDto;

		userInfoDto = keyClockService.getUserInfo(token);

		return new ResponseEntity<>(userInfoDto,HttpStatus.OK);
	}
	/*
	 * When access token get expired than send refresh token to get new access
	 * token. We will receive new refresh token also in this response.Update
	 * client cookie with updated refresh and access token
	 */
	@RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
	public ResponseEntity<?> getTokenUsingRefreshToken(@RequestHeader(value = "Authorization") String refreshToken) {

		String responseToken = null;
		try {
			responseToken = keyClockService.getByRefreshToken(refreshToken);

		} catch (Exception e) {

			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(responseToken, HttpStatus.OK);

	}
}
