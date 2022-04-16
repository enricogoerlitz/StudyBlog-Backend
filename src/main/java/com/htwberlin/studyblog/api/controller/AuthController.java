package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.JWTVerificationResponse;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/** TODO: AuthController -> add the following routes
 *  /auth/isauth (with JWT (validate this) or Cookie (JWT => Validate this))
 *
 */
@Slf4j
@RestController
@RequestMapping(Routes.API + Routes.V1 + Routes.AUTH)
public class AuthController {

    @GetMapping(Routes.HELLO_WORLD)
    public ResponseEntity<String> getTestRoute() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello World: ");
    }

    @GetMapping("/")
    public ResponseEntity<JWTVerificationResponse> getJWTCookie(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(ApplicationJWT.getTokenFromRequest(request));
    }
}
