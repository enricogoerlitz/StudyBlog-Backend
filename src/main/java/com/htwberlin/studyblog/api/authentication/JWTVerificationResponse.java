package com.htwberlin.studyblog.api.authentication;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JWTVerificationResponse {
    private final boolean isValid;
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

    public ApplicationUserModel getUser() {
        return new ApplicationUserModel(
            null,
            extractUsernameFromAuthToken(),
            extractRoleFromAuthToken()
        );
    }

    public JWTVerificationResponse validate() {
        if(!isValid()) throw new AuthorizationServiceException("Current JWT-Token is invalid! \n" + errorMessage);
        return this;
    }

    private String extractRoleFromAuthToken() {
        if(authenticationToken == null) return null;
        var roles = authenticationToken.getAuthorities().toArray();
        if(roles.length != 1) return null;

        return ((SimpleGrantedAuthority)roles[0]).getAuthority();
    }

    private String extractUsernameFromAuthToken() {
        if(authenticationToken == null) return null;
        return authenticationToken.getName();
    }
}
