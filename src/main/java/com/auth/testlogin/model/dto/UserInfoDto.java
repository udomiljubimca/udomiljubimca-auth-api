package com.auth.testlogin.model.dto;

public class UserInfoDto {

    String sub;
    Boolean email_verified;
    String preferred_username;

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Boolean getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(Boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }
}
