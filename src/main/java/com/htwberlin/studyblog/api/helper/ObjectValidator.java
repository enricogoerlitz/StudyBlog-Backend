package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.utilities.ResponseEntityException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

/** ObjectValidator
 *  Static Class for validating objects (is Null) and throwing an exception, if the object is Null.
 */
@Service
@AllArgsConstructor
public class ObjectValidator {
    /**
     * Throws an IllegalArgumentException, if the Object is null
     * @param obj Object for Null-Check
     */
    public void validateNotNullObject(Object obj) {
        if(obj == null)
            throw new IllegalArgumentException("Entity User was null!");
    }

    /**
     * Throws a custom Exception with a custom exceptionMessage, if the passed object is Null.
     * @param obj Object
     * @param exception ResponseEntityException custom exceptionType
     * @param exceptionMessage custom errorMessage
     * @param <T> Object ObjectType
     * @return <T> Object (passed Object)
     * @throws Exception handling exception
     */
    public <T> T getValidObjOrThrowException(T obj, ResponseEntityException exception, String exceptionMessage) throws Exception {
        if(obj == null)
            throwException(exception, exceptionMessage);

        return obj;
    }

    /**
     * Throws a custom exception by the passed exceptionType, with a custom errorMessage.
     * @param exception ResponseEntityException custom exceptionType
     * @param exceptionMessage custom errorMessage
     * @throws Exception handling Exception
     */
    private void throwException(ResponseEntityException exception, String exceptionMessage) throws Exception {
        switch (exception) {
            case USERNAME_NOT_FOUND_EXCEPTION:
                throw new UsernameNotFoundException(exceptionMessage);
            case AUTHENTICATION_EXCEPTION:
                throw new AuthenticationException(exceptionMessage);
            case AUTHORIZATION_SERVICE_EXCEPTION:
                throw new AuthorizationServiceException(exceptionMessage);
            case ILLEGAL_ARGUMENT_EXCEPTION:
                throw new IllegalArgumentException(exceptionMessage);
            case DUPLICATE_KEY_EXCEPTION:
                throw new DuplicateKeyException(exceptionMessage);
            case EXCEPTION:
            default:
                throw new Exception(exceptionMessage);
        }
    }
}
