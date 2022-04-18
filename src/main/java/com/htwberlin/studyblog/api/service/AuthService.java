package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final ApplicationUserRepository userRepository;

    public ApplicationUserModel getCurrentUser(HttpServletRequest request) throws Exception {
        var authUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        return EntityModelTransformer.userEntityToModel(authUser);
    }
}
