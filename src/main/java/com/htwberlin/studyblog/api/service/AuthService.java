package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
     */
    public ApplicationUserModel getCurrentUser(HttpServletRequest request) {
        var user = ApplicationJWT.getUserFromJWT(request);
        if(user == null)
            throw new AuthorizationServiceException("The RequestUser was null!");

        return user;
    }

    /**
     * Register a Visitor for access.
     * Creates a JWT-Token and returned this.
     * @param request http.request
     * @return String JWT-Token
     */
    public String loginVisitor(HttpServletRequest request) {
        return ApplicationJWT.createUserModelToken(
            request,
            new ApplicationUserEntity(-1L, "VisitorUser", null, Role.VISITOR.name())
        );
    }

    /**
     * Create a login for an existing User.
     * Creates a JWT-Token and returned this.
     * Throws an AuthorizationServiceException, if the user is not registered
     * @param request http.request
     * @param authUser ApplicationUserEntity loginUser
     * @return String JWT-Token
     */
    public String loginUser(HttpServletRequest request, ApplicationUserEntity authUser) {
        var dbUser = userRepository.findByUsername(authUser.getUsername());
        if(dbUser == null || !passwordEncoder.matches(authUser.getPassword(), dbUser.getPassword()))
            throw new AuthorizationServiceException("This User ist not registered!");

        return ApplicationJWT.createUserModelToken(request, dbUser);
    }
}
