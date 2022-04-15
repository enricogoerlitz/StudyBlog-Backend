package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.config.ENV;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class AuthController {

    @GetMapping("/helloworld")
    public ResponseEntity<String> getTestRoute() {
        return ResponseEntity.ok().body("Hello World: " + ENV.getJWTSecret());
    }
}
