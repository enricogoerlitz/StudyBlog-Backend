package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.service.AuthService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.AUTHORIZATION_SERVICE_EXCEPTION;


/**
 *     Logout in Client => delete cookie!
 *     update user => refresh cookie in client! => HTTP.POST(/login) -> fetch new Auth JWT
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

    @GetMapping("/")
    public ResponseEntity<ApplicationUserModel> getJWTCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            var user = authService.getCurrentUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        }
    }
}
