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
    public ResponseEntity<List<ApplicationUserModel>> getUsers(HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(request));
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.USERS)
    public ResponseEntity<String> registerUser(HttpServletRequest request, @Valid @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUser(request, newUser, Role.STUDENT.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.ADMIN_USERS)
    public ResponseEntity<ApplicationUserModel> registerUserByAdmin(HttpServletRequest request, @Valid @RequestBody ApplicationUserEntity newUser) {
        try {
            var createdUser = userService.registerUserByAdmin(request, newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_EDIT)
    public ResponseEntity<String> updateUser(HttpServletRequest request, @RequestBody ApplicationUserEntity updatedUser) {
        try {
            var freshUpdatedUser = userService.updateUser(request, updatedUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.USERS_ADMIN_ID)
    public ResponseEntity<ApplicationUserModel> updateUserByAdmin(HttpServletRequest request, @RequestBody ApplicationUserEntity updatedUser, @PathVariable String id) {
        try {
            var freshUpdatedUser = userService.updateUserByAdmin(request, id, updatedUser);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(freshUpdatedUser);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.USERS_ADMIN_ID)
    public ResponseEntity<Void> deleteUserByAdmin(HttpServletRequest request, @PathVariable String id) {
        try {
            userService.deleteUser(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }
}
