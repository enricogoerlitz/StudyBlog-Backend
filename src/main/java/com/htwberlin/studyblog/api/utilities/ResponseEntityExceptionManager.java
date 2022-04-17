package com.htwberlin.studyblog.api.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

public final class ResponseEntityExceptionManager {
    public static <T> ResponseEntity<T> handleException(HttpServletResponse response, ResponseEntityException exceptionType, Exception exp) {
        HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
        switch (exceptionType) {
            case AUTHORIZATION_SERVICE_EXCEPTION:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            case USERNAME_NOT_FOUND_EXCEPTION:
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
}
