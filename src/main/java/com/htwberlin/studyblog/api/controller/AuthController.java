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
import javax.servlet.http.HttpServletResponse;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.AUTHORIZATION_SERVICE_EXCEPTION;
import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.EXCEPTION;

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
    public ResponseEntity<ApplicationUserModel> getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            var currentUser = authService.getCurrentUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(currentUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @GetMapping("/visitor")
    public ResponseEntity<String> getVisitorToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.loginVisitor(request));
    }

    // TODO: Refactor
    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity authUser) {
        try {
            var userToken = authService.loginUser(request, authUser);
            return ResponseEntity.status(HttpStatus.OK).body(userToken);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }
}
