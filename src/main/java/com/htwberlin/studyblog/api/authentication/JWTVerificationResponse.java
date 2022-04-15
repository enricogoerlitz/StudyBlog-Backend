package com.htwberlin.studyblog.api.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JWTVerificationResponse {
    private boolean isValid;
    private UsernamePasswordAuthenticationToken authenticationToken;
    private String errorMessage = "";

    public JWTVerificationResponse(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public JWTVerificationResponse(boolean isValid, UsernamePasswordAuthenticationToken authenticationToken) {
        this.isValid = isValid;
        this.authenticationToken = authenticationToken;
    }

    public boolean isValid() {
        return isValid;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        return authenticationToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
