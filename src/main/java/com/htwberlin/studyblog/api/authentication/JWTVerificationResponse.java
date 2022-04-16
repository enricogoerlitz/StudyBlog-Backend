package com.htwberlin.studyblog.api.authentication;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
public class JWTVerificationResponse {
    private boolean isValid;
    private UsernamePasswordAuthenticationToken authenticationToken;
    private ApplicationUserModel user;
    private String errorMessage = "";

    public JWTVerificationResponse(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public JWTVerificationResponse(boolean isValid, UsernamePasswordAuthenticationToken authenticationToken) {
        this.isValid = isValid;
        this.authenticationToken = authenticationToken;
        this.user = this.getUser();
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
        String role = extractRoleFromAuthToken();
        String username = this.extractUsernameFromAuthToken();

        return new ApplicationUserModel(null, username, role);
    }

    private String extractRoleFromAuthToken() {
        if(this.authenticationToken == null) return null;
        var roles = this.authenticationToken.getAuthorities().toArray();
        if(roles == null || roles.length != 1) return null;
        var authority = (SimpleGrantedAuthority)roles[0];

        return authority.getAuthority();
    }

    private String extractUsernameFromAuthToken() {
        if(this.authenticationToken == null) return null;
        return this.authenticationToken.getName();
    }
}
