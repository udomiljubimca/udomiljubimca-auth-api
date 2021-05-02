package com.auth.testlogin.service;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.dto.UserInfoDto;
import org.keycloak.representations.idm.CredentialRepresentation;

import javax.servlet.ServletRequest;

/**
 * @author Djordje
 * @version 1.0
 */
public interface KeyCloakService {

    TokenDto getToken(UserCredentials userCredentials, ServletRequest request);

    UserInfoDto getUserInfo(String token);

    TokenDto getByRefreshToken(String refreshToken);

    void logoutUser(String userId);

    void resetPassword(ResetPasswordDto resetPasswordDto, String token, String userId);

    void resetPasswordFromAdmin(String newPassword, String userId);

    }