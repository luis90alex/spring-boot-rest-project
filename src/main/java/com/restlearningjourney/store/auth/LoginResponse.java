package com.restlearningjourney.store.auth;


public class LoginResponse {

    private Jwt accessToken;
    private Jwt refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(Jwt accessToken, Jwt refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Jwt getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Jwt accessToken) {
        this.accessToken = accessToken;
    }

    public Jwt getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(Jwt refreshToken) {
        this.refreshToken = refreshToken;
    }
}
