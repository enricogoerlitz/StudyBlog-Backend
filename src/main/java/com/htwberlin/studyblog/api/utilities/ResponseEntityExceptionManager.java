package com.htwberlin.studyblog.api.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ResponseEntityExceptionManager<T> {
    public static <T> ResponseEntity<T> handleException(HttpServletResponse response, ResponseEntityException exceptionType, Exception exp) {
        writeError(response, exp);
        switch (exceptionType) {
            case AUTHORIZATION_SERVICE_EXCEPTION:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            case ILLEGAL_ARGUMENT_EXCEPTION:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            case AUTHENTICATION_EXCEPTION:
            case DUPLICATE_KEY_EXCEPTION:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            case EXCEPTION:
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static void writeError(HttpServletResponse response, Exception exp) {
        try {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
        } catch (IOException e) {}
    }
}
