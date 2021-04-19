package com.auth.testlogin.service;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import org.keycloak.representations.idm.CredentialRepresentation;

import javax.servlet.ServletRequest;

/**
 * @author Djordje
 * @version 1.0
 */
public interface KeyCloakService {

    TokenDto getToken(UserCredentials userCredentials, ServletRequest request) throws Exception;

    void logoutUser(String userId);

    void resetPassword(CredentialRepresentation cr, String token, String userId);
}
