package com.auth.testlogin.service;

import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.ResetPasswordDto;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.dto.UserInfoDto;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import java.util.LinkedHashMap;

/**
 * @author Djordje
 * @version 1.0
 */
@Component
public class KeyCloakServiceImpl implements KeyCloakService {

    @Value("${keycloak.credentials.secret}")
    private String SECRETKEY;

    @Value("${keycloak.resource}")
    private String CLIENTID;

    @Value("${keycloak.auth-server-url}")
    private String AUTHURL;

    @Value("${keycloak.realm}")
    private String REALM;

    @Autowired
    private RestTemplate restTemplate;

    public TokenDto getToken(UserCredentials userCredentials, ServletRequest request) {

        TokenDto tokenDto;
        try {
            MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
            mapForm.add("grant_type", "password");
            mapForm.add("client_id", CLIENTID);
            mapForm.add("username", userCredentials.getUsername());
            mapForm.add("password", userCredentials.getPassword());
            mapForm.add("client_secret", SECRETKEY);

            //get token
            tokenDto = exchange(mapForm);

            //get user info by access token
            if (tokenDto != null) {
                tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
            }
            return tokenDto;

        } catch (Exception e) {
            throw new WrongUserCredentialsException("Wrong credentials, please try again!");
        }
    }

    public UserInfoDto getUserInfo(String token) {

        if (token == null) {
            throw new TokenNotValidException("Cannot load user info because token is not present!");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(token, headers);

        UserInfoDto userInfoDto = restTemplate.exchange(
                AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/userinfo",
                HttpMethod.GET, entity, UserInfoDto.class).getBody();

        return userInfoDto;
    }

    public TokenDto getByRefreshToken(String refreshToken) {
        TokenDto tokenDto;
        try {
            MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
            mapForm.add("client_id", CLIENTID);
            mapForm.add("grant_type", "refresh_token");
            mapForm.add("refresh_token", refreshToken.substring(7));
            mapForm.add("client_secret", SECRETKEY);

            tokenDto = exchange(mapForm);

            if (tokenDto != null) {
                tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
            }

        } catch (Exception e) {
            throw new TokenNotValidException("Refresh token is not present or not valid! Please login again.");
        }
        return tokenDto;
    }

    // after logout user from the keycloak system. No new access token will be issued
    public void logoutUser(String refreshToken) {

        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", CLIENTID);
            requestParams.add("client_secret", SECRETKEY);
            requestParams.add("refresh_token", refreshToken.substring(7));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);

            String url = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/logout";

            restTemplate.postForEntity(url, request, Object.class);

        } catch (Exception e) {
            throw new TokenNotValidException("Error while logout user, provided refresh token is not valid!");
        }
    }

    // Reset passowrd
    public void resetPassword(ResetPasswordDto resetPasswordDto, String token, String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            CredentialRepresentation cr = new CredentialRepresentation();
            cr.setType(CredentialRepresentation.PASSWORD);
            cr.setTemporary(false);
            cr.setValue(resetPasswordDto.getPassword());
            HttpEntity<CredentialRepresentation> entity = new HttpEntity<>(cr, headers);

            restTemplate.put(AUTHURL + "/admin/realms/" + REALM + "/users/" + userId + "/reset-password",
                    entity);
        } catch (Exception e) {
            throw new TokenNotValidException("Something went wrong, please log out and try to login again! Check token adn userId!");
        }
    }

    // New method for exchange using Rest Template
    private TokenDto exchange(MultiValueMap<String, String> mapForm) {

        TokenDto tokenDto = new TokenDto();

        ResponseEntity<Object> response = null;
        String uri = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        response = restTemplate.exchange(uri, HttpMethod.POST, request, Object.class);
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) response.getBody();

        if (map != null) {
            tokenDto.setAccessToken(map.get("access_token").toString());
            tokenDto.setTokenType(map.get("token_type").toString());
            tokenDto.setRefreshToken(map.get("refresh_token").toString());
            tokenDto.setExpires_in(map.get("expires_in").toString());
            tokenDto.setScope(map.get("scope").toString());
        } else {
            return null;
        }
        return tokenDto;
    }
}
