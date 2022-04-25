package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.AuthService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/** AuthController
 *  RESTController for Authentication-Routes
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.API + Routes.V1 + Routes.AUTH)
public class AuthController {
    private final AuthService authService;

    @GetMapping(Routes.HELLO_WORLD)
    public ResponseEntity<String> getTestRoute() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World: ");
    }

    @GetMapping("/user")
    public ResponseEntity<ApplicationUserModel> getCurrentUser(HttpServletRequest request) {
        try {
            var currentUser = authService.getCurrentUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(currentUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHENTICATION_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        }
    }

    @GetMapping("/visitor")
    public ResponseEntity<String> getVisitorToken(HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(authService.loginVisitor(request));
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestBody ApplicationUserEntity authUser) {
        try {
            var userToken = authService.loginUser(request, authUser);
            return ResponseEntity.status(HttpStatus.OK).body(userToken);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(AUTHENTICATION_EXCEPTION, exp);
        }
    }
}
