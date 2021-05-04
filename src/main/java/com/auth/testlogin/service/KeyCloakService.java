package com.auth.testlogin.service;

import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.dto.UserInfoDto;

/**
 * @author Djordje
 * @version 1.0
 */
public interface KeyCloakService {

    TokenDto getToken(UserCredentials userCredentials);

    UserInfoDto getUserInfo(String token);

    TokenDto getByRefreshToken(String refreshToken);

    void logoutUser(String userId);

    void resetPasswordFromAdmin(String newPassword, String userId);

}