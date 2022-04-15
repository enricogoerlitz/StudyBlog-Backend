package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.utilities.ENV;
import com.htwberlin.studyblog.api.utilities.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TODO: AuthController -> add the following routes
 *  /auth/isauth (with JWT (validate this) or Cookie (JWT => Validate this))
 *
 */
@RestController
@RequestMapping(Routes.AUTH)
public class AuthController {

    @GetMapping(Routes.HELLO_WORLD)
    public ResponseEntity<String> getTestRoute() {
        return ResponseEntity.ok().body("Hello World: ");
    }
}
