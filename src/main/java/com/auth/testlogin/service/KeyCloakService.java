package com.auth.testlogin.service;

import com.auth.testlogin.model.dto.TokenDto;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.UserInfoDto;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
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
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.keycloak.admin.client.Keycloak.getInstance;

@Component
public class KeyCloakService {

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

    // Get Token
    public TokenDto getToken(UserCredentials userCredentials, ServletRequest request) throws Exception {

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
            throw new Exception(e.getMessage());
        }
    }

    public UserInfoDto getUserInfo(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(token, headers);

        UserInfoDto userInfoDto = restTemplate.exchange(
                AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/userinfo",
                HttpMethod.GET, entity, UserInfoDto.class).getBody();

        return userInfoDto;
    }

    // Should be updated with exchange method instead sendPost
    public String getByRefreshToken(String refreshToken) {
        String responseToken = null;
        try {
            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
            urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
            urlParameters.add(new BasicNameValuePair("refresh_token", refreshToken));
            urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));

            responseToken = sendPost(urlParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseToken;
    }

    // after logout user from the keycloak system. No new access token will be issued
    public void logoutUser(String userId) {

        UsersResource userRessource = getKeycloakUserResource();
        userRessource.get(userId).logout();

    }

    // Reset passowrd
    // Should be tested once when Principal available on client side
    public void resetPassword(String newPassword, String userId) {

        UsersResource userResource = getKeycloakUserResource();

        // Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword.toString().trim());

        // Set password credential
        userResource.get(userId).resetPassword(passwordCred);

    }

    private UsersResource getKeycloakUserResource() {

        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm(REALM).username("admin").password("admin")
                .clientId(CLIENTID).resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        RealmResource realmResource = kc.realm(REALM);
        UsersResource userRessource = realmResource.users();

        return userRessource;
    }

    // New method for exchange using Rest Template
    private TokenDto exchange(MultiValueMap<String, String> mapForm) {

        TokenDto tokenDto = new TokenDto();

        String uri = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);
        ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, request, Object.class);
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

    //This method is deprecated and should be updated on exchange but here is until we testing
    private String sendPost(List<NameValuePair> urlParameters) throws Exception {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token");

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

}
