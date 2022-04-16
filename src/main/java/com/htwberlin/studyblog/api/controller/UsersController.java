package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import com.htwberlin.studyblog.api.utilities.HttpResponseWriter;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** TODO: UsersController -> add folloing routes
 *  /users/delete?{id}  *&&*    /admin/users/delete?{id}
 *  /users/edit?{id}    *&&*    /admin/edit?{id}
 */

@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final ApplicationUserService userService;

    @GetMapping(Routes.ADMIN_USERS)
    public ResponseEntity<List<ApplicationUserEntity>> getUsers() {
        var users = userService.getUsers();
        if(users == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(users);
    }

    @PostMapping(Routes.USERS)
    public ResponseEntity<ApplicationUserEntity> registerUser(@RequestBody ApplicationUserEntity newUser) {
        newUser.setRole(Role.STUDENT.name());
        var createdUser = userService.registerUser(newUser);
        if(createdUser == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.created(null).body(createdUser);
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserEntity> registerUserByAdmin(@RequestBody ApplicationUserEntity newUser) {
        var createdUser = userService.registerUser(newUser);
        if(createdUser == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.created(null).body(createdUser);
    }

    @PutMapping("/v1/users/edit")
    public ResponseEntity<ApplicationUserEntity> updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity updatedUser) throws IOException {
        try {
            var freshUpdatedUser = userService.updateUser(request, response, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/v1/admin/users/{id}")
    public ResponseEntity<ApplicationUserEntity> updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity updatedUser, @PathVariable String id) throws IOException {
        try {
            var freshUpdatedUser = userService.updateUserByAdmin(request, response, id, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/v1/admin/users/{id}")
    public ResponseEntity<Void> deleteUserByAdmin(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException {
        try {
            userService.deleteUser(request, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AuthorizationServiceException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
