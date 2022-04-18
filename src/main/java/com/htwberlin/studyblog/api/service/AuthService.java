package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

/** AuthService
 *  Service for Authentication BusinessLogic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final ApplicationUserRepository userRepository;

    /**
     * Tries to fetch the Current-Auth-User, based on the Request-JWT-Token.
     * Returns ApplicationUserModel, if user is existing and authenticated.
     * If user is not authenticated or not existing, this method throws an exception.
     * @param request http.request
     * @return ApplicationUserModel Current Authenticated UserModel
     * @throws Exception handle exception
     */
    public ApplicationUserModel getCurrentUser(HttpServletRequest request) throws Exception {
        var authUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        return EntityModelTransformer.userEntityToModel(authUser);
    }
}
