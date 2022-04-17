package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/**
 *      update user => refresh cookie in client! => HTTP.POST(/login) -> fetch new Auth JWT
 */
@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
@Slf4j
public class UsersController {
    private final ApplicationUserService userService;

    @GetMapping(Routes.ADMIN_USERS)
    public ResponseEntity<List<ApplicationUserEntity>> getUsers(HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.USERS)
    public ResponseEntity<ApplicationUserEntity> registerUser(HttpServletResponse response, @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUser(newUser, Role.STUDENT.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserEntity> registerUserByAdmin(HttpServletResponse response, @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_EDIT)
    public ResponseEntity<ApplicationUserEntity> updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity updatedUser) {
        try {
            var freshUpdatedUser = userService.updateUser(request, response, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_ADMIN_ID)
    public ResponseEntity<ApplicationUserEntity> updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity updatedUser, @PathVariable String id) {
        try {
            var freshUpdatedUser = userService.updateUserByAdmin(request, response, id, updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @DeleteMapping("/v1/admin/users/{id}")
    public ResponseEntity<Void> deleteUserByAdmin(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            userService.deleteUser(request, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }
}
