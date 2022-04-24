package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/** UsersController
 *  RESTController for User-Routes
 */
@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
public class UsersController {
    private final ApplicationUserService userService;

    @GetMapping(Routes.ADMIN_USERS)
    public ResponseEntity<List<ApplicationUserModel>> getUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(request));
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.USERS)
    public ResponseEntity<ApplicationUserModel> registerUser(HttpServletResponse response, @Valid @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUser(newUser, Role.STUDENT.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserModel> registerUserByAdmin(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUserByAdmin(request, newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_EDIT)
    public ResponseEntity<String> updateUser(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody ApplicationUserEntity updatedUser) {
        try {
            var freshUpdatedUser = userService.updateUser(request, updatedUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_ADMIN_ID)
    public ResponseEntity<ApplicationUserModel> updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody ApplicationUserEntity updatedUser, @PathVariable String id) {
        try {
            var freshUpdatedUser = userService.updateUserByAdmin(request, id, updatedUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.USERS_ADMIN_ID)
    public ResponseEntity<Void> deleteUserByAdmin(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            userService.deleteUser(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }
}
