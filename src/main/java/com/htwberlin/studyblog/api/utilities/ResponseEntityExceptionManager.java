package com.htwberlin.studyblog.api.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** ResponseEntityExceptionManager
 *  Static Class for handle ResponseEntityExceptions and sent correct statusCodes
 */
@Slf4j
public final class ResponseEntityExceptionManager {
    public static <T> ResponseEntity<T> handleException(ResponseEntityException exceptionType, Exception exp) {
        log.error("EXCEPTION: " + exp.getMessage());
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
