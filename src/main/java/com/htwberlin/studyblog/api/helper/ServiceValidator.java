package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.utilities.ResponseEntityException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.AUTHORIZATION_SERVICE_EXCEPTION;
import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.USERNAME_NOT_FOUND_EXCEPTION;

public final class ServiceValidator {
    public static ApplicationUserModel getValidRequestUser(HttpServletRequest request) throws Exception {
        return (ApplicationUserModel) getValidObjOrThrowException(
            ApplicationJWT.getUserFromJWT(request),
            AUTHORIZATION_SERVICE_EXCEPTION,
            "JWT-Token was not valid!"
        );
    }

    public static ApplicationUserEntity getValidDbUserByUsername(ApplicationUserRepository userRepository, String username) throws Exception {
        return (ApplicationUserEntity) getValidObjOrThrowException(
            userRepository.findByUsername(username),
            USERNAME_NOT_FOUND_EXCEPTION,
            "Could not find user with username " + username + " in the DB!"
        );
    }

    public static ApplicationUserEntity getValidDbUserFromRequest(HttpServletRequest request, ApplicationUserRepository userRepository) throws Exception {
        var requestUser = getValidRequestUser(request);
        return getValidDbUserByUsername(userRepository, requestUser.getUsername());
    }

    public static Object getValidObjOrThrowException(Object obj, ResponseEntityException exception, String exceptionMessage) throws Exception {
        if(obj == null) throwException(exception, "User not found in DB!");

        return obj;
    }

    private static void throwException(ResponseEntityException exception, String exceptionMessage) throws Exception {
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
