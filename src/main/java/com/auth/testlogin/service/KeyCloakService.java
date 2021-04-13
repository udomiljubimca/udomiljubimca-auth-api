package com.auth.testlogin.service;

import com.auth.testlogin.model.TokenDto;
import com.auth.testlogin.model.UserCredentials;
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

import javax.security.auth.login.CredentialException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

    public TokenDto getToken(UserCredentials userCredentials) throws Exception {

        TokenDto tokenDto = new TokenDto();
        String uri = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)));

        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", "password");
        mapForm.add("client_id", CLIENTID);
        mapForm.add("username", userCredentials.getUsername());
        mapForm.add("password", userCredentials.getPassword());
        mapForm.add("client_secret", SECRETKEY);
        try {
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
                throw new CredentialException("Invalid credentials!");
            }
            return tokenDto;
        } catch (Exception e) {
            throw new CredentialException("Invalid credentials!");
        }
    }

//    public String getToken(UserCredentials userCredentials) {
//        String responseToken = null;
//        try {
//
//            String username = userCredentials.getUsername();
//
//            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//            urlParameters.add(new BasicNameValuePair("grant_type", "password"));
//            urlParameters.add(new BasicNameValuePair("client_id", CLIENTID));
//            urlParameters.add(new BasicNameValuePair("username", username));
//            urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));
//            urlParameters.add(new BasicNameValuePair("client_secret", SECRETKEY));
//
//            responseToken = sendPost(urlParameters);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return responseToken;
//    }

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

    // after logout user from the keycloak system. No new access token will be
    // issued.
    public void logoutUser(String userId) {

        UsersResource userRessource = getKeycloakUserResource();
        userRessource.get(userId).logout();

    }

    // Reset passowrd
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

        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTHURL).realm("master").username("admin").password("admin")
                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        RealmResource realmResource = kc.realm(REALM);
        UsersResource userRessource = realmResource.users();

        return userRessource;
    }

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
