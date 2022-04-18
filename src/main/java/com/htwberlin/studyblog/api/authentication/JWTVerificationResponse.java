package com.htwberlin.studyblog.api.authentication;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** JWTVerificationResponse
 *  Class for JWT-Verification Response.
 *  (is Token Valid, validate this Token, ErrorMessage, AuthenticationToken)
 */
public class JWTVerificationResponse {
    private final boolean isValid;
    private UsernamePasswordAuthenticationToken authenticationToken;
    private String errorMessage = "";

    /**
     * Constructor for invalid tokens.
     * @param isValid boolean is token valid
     * @param errorMessage if token is invalid, save the errormessage
     */
    public JWTVerificationResponse(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor for valid tokens.
     * @param isValid boolean is token valid
     * @param authenticationToken UsernamePasswordAuthenticationToken
     */
    public JWTVerificationResponse(boolean isValid, UsernamePasswordAuthenticationToken authenticationToken) {
        this.isValid = isValid;
        this.authenticationToken = authenticationToken;
    }

    /**
     * Returns whether the token is valid.
     * @return boolean is token valid.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Returns the UsernamePasswordAuthenticationToken if the token is valid.
     * @return UsernamePasswordAuthenticationToken or Null.
     */
    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        return authenticationToken;
    }

    /**
     * Returns the ValidationError, if the token is invalid.
     * @return String with ValidationError or Null.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the JWTUser, of the token is valid.
     * @return JWTUser or Null.
     */
    public JWTUser getUser() {
        return new JWTUser(
            extractUsernameFromAuthToken(),
            extractRoleFromAuthToken()
        );
    }

    /**
     * Throws an AuthorizationServiceException if the token is invalid.
     * @return JWTVerificationResponse This Object if the token was valid.
     */
    public JWTVerificationResponse validate() throws AuthorizationServiceException {
        if(!isValid())
            throw new AuthorizationServiceException("Current JWT-Token is invalid! \n" + errorMessage);

        return this;
    }

    /**
     * Returns the Role of the authenticated user, if the token was valid. Else Null
     * @return String Role of authenticated user or Null
     */
    private String extractRoleFromAuthToken() {
        if(authenticationToken == null)
            return null;

        var roles = authenticationToken.getAuthorities().toArray();
        if(roles.length == 0)
            return null;

        return ((SimpleGrantedAuthority)roles[0]).getAuthority();
    }

    /**
     * Returns the Username of the authenticated user, if the token was valid. Else Null
     * @return String Username of authenticated user or Null
     */
    private String extractUsernameFromAuthToken() {
        if(authenticationToken == null) return null;
        return authenticationToken.getName();
    }
}
