package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsersController {
    private final ApplicationUserService userService;

    @GetMapping("/v1/users")
    public ResponseEntity<List<ApplicationUserModel>> getUsers() {
        var users = userService.getUsers();
        if(users == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(users);
    }

    @PostMapping("/v1/users")
    public ResponseEntity<ApplicationUserModel> registerUser(@RequestBody ApplicationUserModel user) {
        var createdUser = userService.registerUser(user);
        if(createdUser == null) return ResponseEntity.badRequest().build();

        // TODO: test the uri and applicationusermodel
        // URI uri = URI.create(ServletUriComponentsBuilder.fromContextPath(null).path("/api/v1/users").toUriString())
        // no applicationUsrModel!
        return ResponseEntity.created(null).body(createdUser);
    }
}
