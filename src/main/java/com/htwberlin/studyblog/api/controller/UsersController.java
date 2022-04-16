package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** TODO: UsersController -> add folloing routes
 *  /users/delete?{id}  *&&*    /admin/users/delete?{id}
 *  /users/edit?{id}    *&&*    /admin/edit?{id}
 */

/** TODO: Controller-Package -> add folling Controllers
 *  BlogPostController
 *      /posts/add
 *      /posts/edit?{id}                (if me is the creator oder admin)
 *      /posts/delete?{id}              (if me is the creator oder admin)
 *      /posts/add_favourite?{id}       (if not mine)
 *      /posts/remove_favourite?{id}    (if not mine and is favourite)
 */
@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
public class UsersController {
    private final ApplicationUserService userService;

    @GetMapping(Routes.ADMIN_USERS)
    public ResponseEntity<List<ApplicationUserEntity>> getUsers() {
        var users = userService.getUsers();
        if(users == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(users);
    }

    @PostMapping(Routes.USERS)
    public ResponseEntity<ApplicationUserEntity> registerUser(@RequestBody ApplicationUserEntity user) {
        user.setRole(Role.STUDENT.name());
        var createdUser = userService.registerUser(user);
        if(createdUser == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.created(null).body(createdUser);
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserEntity> registerUserByAdmin(@RequestBody ApplicationUserEntity user) {
        var createdUser = userService.registerUser(user);
        if(createdUser == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.created(null).body(createdUser);
    }
}
