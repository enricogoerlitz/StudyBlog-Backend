package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
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

    @PostMapping(Routes.USERS)
    public ResponseEntity<ApplicationUserModel> registerUser(@RequestBody ApplicationUserModel user) {
        user.setRole(Role.STUDENT.name());
        var createdUser = userService.registerUser(user);
        if(createdUser == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.created(null).body(createdUser);
    }

    @GetMapping(Routes.ADMIN_USERS)
    public ResponseEntity<List<ApplicationUserModel>> getUsers() {
        var users = userService.getUsers();
        if(users == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(users);
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserModel> registerUserByAdmin(@RequestBody ApplicationUserModel user) {
        var createdUser = userService.registerUser(user);
        if(createdUser == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.created(null).body(createdUser);
    }
}
