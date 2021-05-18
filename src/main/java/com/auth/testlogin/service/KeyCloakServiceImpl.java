package com.auth.testlogin.service;

import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.dto.UserInfoDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    @Value("${admin.username}")
    private String ADMIN_USERNAME;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Getting token for particular user and getting user info within response
     *
     * @param userCredentials username and password from body
     */
    public TokenDto getToken(UserCredentials userCredentials) {

        TokenDto tokenDto;
        try {
            MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
            mapForm.add("grant_type", "password");
            mapForm.add("client_id", CLIENTID);
            mapForm.add("username", userCredentials.getUsername());
            mapForm.add("password", userCredentials.getPassword());
            mapForm.add("client_secret", SECRETKEY);

            tokenDto = exchange(mapForm);

            if (tokenDto != null) {
                tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
            }
            return tokenDto;

        } catch (Exception e) {

            throw new WrongUserCredentialsException(e.getMessage());

        }
    }

    /**
     * Getting user info: id (sub), email_verified, preferred_username
     *
     * @param token user token form Authorization header
     */
    public UserInfoDto getUserInfo(String token) {

        if (token == null) {
            throw new TokenNotValidException("Cannot load user info because token is not present or valid!");
        }

        UserInfoDto userInfoDto = new UserInfoDto();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(token, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/userinfo",
                    HttpMethod.GET, entity, Object.class);

            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) response.getBody();

            if (map != null) {
                userInfoDto.setSub(map.get("sub").toString());
                userInfoDto.setEmail_verified(map.get("email_verified").toString().equalsIgnoreCase("true"));
                userInfoDto.setPreferred_username(map.get("preferred_username").toString());
                userInfoDto.setEmail(map.get("email").toString());
                userInfoDto.setName(map.get("name").toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new TokenNotValidException("Please check your login! " + e.getMessage());
        }
        return userInfoDto;
    }

    /**
     * Getting token using refresh token, old token will be used until expiration time
     *
     * @param refreshToken refresh token
     */
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

    /**
     * Logout user using refresh token, and invalidate tokens
     *
     * @param refreshToken refresh token
     */
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

    /**
     * Method resetPasswordFromAdmin, reset password using Admin user
     *
     * @param newPassword new password
     * @param userId      userId who wants reset
     */
    public void resetPasswordFromAdmin(String newPassword, String userId) {

        UsersResource userResource = getKeycloakUserResource();

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword.trim());

        try {
            userResource.get(userId).resetPassword(passwordCred);
        } catch (Exception e) {
            throw new WrongUserCredentialsException("Admin credentials are not valid!");
        }

    }

    /**
     * Method exchange, exchanges data with Keycloak and gets Token response
     *
     * @param mapForm function parameter
     */
    private TokenDto exchange(MultiValueMap<String, String> mapForm) throws Exception {

        TokenDto tokenDto = new TokenDto();

        ResponseEntity<Object> response;
        String uri = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        try {
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
        } catch (HttpClientErrorException.BadRequest e) {

            throw new WrongUserCredentialsException("Check your email inbox and verify your account!");

        } catch (HttpClientErrorException.Unauthorized e) {

            throw new WrongUserCredentialsException("Wrong credentials!");

        } catch (Exception e) {

            throw new Exception("Something went wrong, please try again!");
        }
        return tokenDto;
    }

    /**
     * Method getKeycloakUserResource, Create Admin from KeycloakBuilder and get User resource
     */
    private UsersResource getKeycloakUserResource() {

        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(AUTHURL)
                .realm("master")
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        RealmResource realmResource = kc.realm(REALM);

        return realmResource.users();
    }

}
