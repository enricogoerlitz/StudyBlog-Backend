package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

/** AuthService
 *  Service for Authentication BusinessLogic
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tries to fetch the Current-Auth-User, based on the Request-JWT-Token.
     * Returns ApplicationUserModel, if user is existing and authenticated.
     * If user is not authenticated or not existing, this method throws an exception.
     * @param request http.request
     * @return ApplicationUserModel Current Authenticated UserModel
     * @throws Exception handle exception
     */
    public ApplicationUserModel getCurrentUser(HttpServletRequest request) throws Exception {
        var user = ApplicationJWT.getUserFromJWT(request);
        if(user == null)
            throw new AuthorizationServiceException("The RequestUser was null!");
        return user;
    }

    public String loginUser(HttpServletRequest request, ApplicationUserEntity authUser) {
        var dbUser = userRepository.findByUsername(authUser.getUsername());
        if(dbUser == null || !passwordEncoder.matches(authUser.getPassword(), dbUser.getPassword()))
            throw new AuthorizationServiceException("User ist not authorized!");
        // set cookie
        String token = ApplicationJWT.createUserModelToken(request, dbUser);
        return ApplicationJWT.createUserModelToken(request, dbUser);
    }
}
