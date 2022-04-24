package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.AuthService;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** AuthController
 *  RESTController for Authentication-Routes
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.API + Routes.V1 + Routes.AUTH)
@Slf4j
public class AuthController {
    private final AuthService authService;

    @GetMapping(Routes.HELLO_WORLD)
    public ResponseEntity<String> getTestRoute() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World: ");
    }

    /*
    @GetMapping("/")
    public ResponseEntity<ApplicationUserModel> getJWTCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            var user = authService.getCurrentUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        }
    }
     */

    // TODO: Refactor
    @GetMapping("/user")
    public ResponseEntity<ApplicationUserModel> getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            var currentUser = authService.getCurrentUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(currentUser);
        } catch (Exception exp) {
            log.error(exp.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
        } catch(Exception exp) {
            log.error("expMsg: " + exp.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exp.getMessage());
        }
    }
}
