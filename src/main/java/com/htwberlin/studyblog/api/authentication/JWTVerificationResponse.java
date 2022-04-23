package com.htwberlin.studyblog.api.authentication;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import org.springframework.security.access.AuthorizationServiceException;

/** JWTVerificationResponse
 *  Class for JWT-Verification Response.
 *  (is Token Valid, validate this Token, ErrorMessage, AuthenticationToken)
 */
public class JWTVerificationResponse {
    private final boolean isValid;
    private String errorMessage = "";
    private ApplicationUserModel user;

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
     * @param user ApplicationUserModel
     */
    public JWTVerificationResponse(boolean isValid, ApplicationUserModel user) {
        this.isValid = isValid;
        this.user = user;
    }

    /**
     * Returns whether the token is valid.
     * @return boolean is token valid.
     */
    public boolean isValid() {
        return isValid;
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
    public ApplicationUserModel getUser() {
        return user;
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
}
