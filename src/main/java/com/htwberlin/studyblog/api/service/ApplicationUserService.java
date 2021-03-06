package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.EntityModelTransformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.helper.ObjectValidator;
import com.htwberlin.studyblog.api.helper.PathVariableParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.EXCEPTION;

/** ApplicationUserService
 *  Service for ApplicationUser BusinessLogic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationUserService {
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationJWT appJWT;
    private final ServiceValidator serviceValidator;
    private final EntityModelTransformer transformer;
    private final PathVariableParser pathVarParser;
    private final ObjectValidator objValidator;

    /**
     * Fetches all users from the DB and transforms the entities to models.
     * @return List<ApplicationUserModel>
     */
    public List<ApplicationUserModel> getUsers(HttpServletRequest request) throws Exception {
        serviceValidator.validateIsUserInRole(request, userRepository, Role.ADMIN);
        return transformer.userEntitiesToModels(userRepository.findAll());
    }

    /**
     * Saves a new User with an initial role to the DB. This method is for Users, who are registering themselves.
     * @param user ApplicationUserEntity The userdata, which should be registered
     * @param initialRole String The role, what the user should get
     * @return ApplicationUserModel
     */
    public String registerUser(HttpServletRequest request, ApplicationUserEntity user, String initialRole) throws Exception {
        user.setRole(initialRole);
        return appJWT.createUserModelToken(request, registerUser(user));
    }

    /**
     * This route is for admin-users only.
     * Saves a new User with an encrypted password to the DB. This method is for Admins, who register a new User.
     * @param user The userdata, which should be registered (by admin, incl. role)
     * @return ApplicationUserModel
     */
    public ApplicationUserEntity registerUser(ApplicationUserEntity user) throws Exception {
        validateRole(user.getRole());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * This route is for admin-users only.
     * Validates, that the requestUser is an admin.
     * @param request http.request
     * @param user ApplicationUserEntity
     * @return ApplicationUserModel registered User
     * @throws Exception handle exception
     */
    public ApplicationUserModel registerUserByAdmin(HttpServletRequest request, ApplicationUserEntity user) throws Exception {
        serviceValidator.validateIsUserInRole(request, userRepository, Role.ADMIN);
        return transformer.userEntityToModel(registerUser(user));
    }

    /**
     * This Route manipulates the data of the request-user.
     * Updates the logged user (from JWT-Token).
     * It updates the username and password, if they had changed.
     * Fetches the user from the db by the JWT-Request-Token.
     * @param request http.request
     * @param updatedUser the userdata, which should updated to the DB.
     * @return ApplicationUserModel
     * @throws Exception handling exception
     */
    public String updateUser(HttpServletRequest request, ApplicationUserEntity updatedUser) throws Exception {
        var manipulationDbUser = serviceValidator.getValidDbUserFromRequest(request, userRepository, Role.STUDENT, Role.ADMIN);
        validateRole(manipulationDbUser.getRole());
        updatedUser.setId(manipulationDbUser.getId());
        updatedUser.setRole(manipulationDbUser.getRole());
        changeUsername(manipulationDbUser, updatedUser);
        changePassword(manipulationDbUser, updatedUser);

        var savedUpdatedUser = objValidator.getValidObjOrThrowException(
            userRepository.save(manipulationDbUser),
            EXCEPTION,
            "User could not be updated!"
        );

        return appJWT.createUserModelToken(request, savedUpdatedUser);
    }

    /**
     * This route is for admin-users only.
     * This route manipulates the data of the passed userId.
     * Updating the username, password and role of a user (by userId).
     * admins can modify students and themselves
     * admins can't modify other admins, only themselves
     * admins can modify themselves, but can't remove/change there role to a student, visitor oder any other role
     *
     * @param request http.request
     * @param id id of the user, which should be updated by an admin
     * @param updatedUser the userdata, which should updated to the DB.
     * @return ApplicationUserEntity
     * @throws Exception handling exception
     */
    public ApplicationUserModel updateUserByAdmin(HttpServletRequest request, String id, ApplicationUserEntity updatedUser) throws Exception {
        serviceValidator.validateIsUserInRole(request, userRepository, Role.ADMIN);
        Long dbUserId = pathVarParser.parseLong(id);
        validateUsername(updatedUser.getUsername());
        var manipulationDbUser = serviceValidator.getValidDbUserById(userRepository, dbUserId);

        if(isManipulationUserAdmin(manipulationDbUser))
            checkRequestUserIsAuthorizedForAdminManipulation(request, manipulationDbUser, updatedUser);

        changeUsername(manipulationDbUser, updatedUser);
        changePassword(manipulationDbUser, updatedUser);
        changeRole(manipulationDbUser, updatedUser);

        return transformer.userEntityToModel(userRepository.save(manipulationDbUser));
    }

    /**
     * This route is for admin-users only.
     * Admins can delete users, incl. themselves.
     * But admins can't delete other admin-user.
     * Deletes all references to this userId in the DB
     *
     * @param request http.request
     * @param id String id of the user, which should be deleted
     * @throws Exception handling exception
     */
    public void deleteUser(HttpServletRequest request, String id) throws Exception {
        Long userId = pathVarParser.parseLong(id);
        var delDbUser = serviceValidator.getValidDbUserById(userRepository, userId, Role.STUDENT, Role.ADMIN);

        if(isManipulationUserAdmin(delDbUser))
            checkRequestUserIsAuthorizedToDeleteAdminUser(request, delDbUser);

        favoriteRepository.deleteAllByCreator_Id(userId);
        blogPostRepository.deleteAllByCreator_Id(userId);
        userRepository.deleteById(userId);
    }

    /**
     * Changed the username of the manipulationDbUser to the username of updatedUser, if the usernames are different.
     * If the usernames are different, validate, that the username of the updatedUser is not existing in the DB.
     * If it is not existing, change the username.
     * If it is existing, throw an IllegalArgumentException.
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param updatedUser ApplicationUserEntity updatedUser
     */
    private void changeUsername(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) {
        if(manipulationDbUser.getUsername().equals(updatedUser.getUsername())) return;

        var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
        if(isUsernameExisting != null)
            throw new IllegalArgumentException("This Username is still existing!");

        manipulationDbUser.setUsername(updatedUser.getUsername());
    }

    /**
     * Changed the password of the manipulationDbUser to the password of the updatedUser.
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param updatedUser ApplicationUserEntity updatedUser
     */
    private void changePassword(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        if(updatedUser.getPassword().isBlank()) return;
        validatePassword(updatedUser.getPassword());
        manipulationDbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }

    /**
     * Changed the role of the manipulationDbUser to the role of the updatedUser.
     * Validates the role updatedUser and throws an exception, if the role is invalid.
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param updatedUser ApplicationUserEntity updatedUser
     * @throws Exception handling exception
     */
    private void changeRole(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        if(updatedUser.getRole() == null) return;
        validateRole(updatedUser.getRole());
        manipulationDbUser.setRole(updatedUser.getRole());
    }

    /**
     * Checks, whether the manipulationUser ha the admin-role.
     * Returns True, if the user has the admin-role, else False
     * @param manipulationUser ApplicationUserEntity manipulationUser
     * @return boolean
     */
    private boolean isManipulationUserAdmin(ApplicationUserEntity manipulationUser) {
        return manipulationUser.getRole().equals(Role.ADMIN.name());
    }

    /**
     * This method will be called, if the user, which want to be manipulated, has the admin-role.
     * Checks, whether the request-user (manipulationDbUser) ist also the updatedUser and so authorized, to manipulate an admin-user.
     * Checks, that the role has not changed. You can't change the role of an admin-user!
     * If one of the checks failed, this method will throw an exception.
     * @param request http.request
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param updatedUser ApplicationUserEntity updatedUser
     * @throws Exception handle exception
     */
    private void checkRequestUserIsAuthorizedForAdminManipulation(HttpServletRequest request, ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        var requestUser = serviceValidator.getValidDbUserFromRequest(request, userRepository);
        validateManipulationDbUserIsRequestUser(manipulationDbUser, requestUser);
        validateManipulationUserHasNotChangedRole(manipulationDbUser, updatedUser);
    }

    /**
     * Checks, whether the request-user (manipulationDbUser) ist also the updatedUser and so authorized, to manipulate an admin-user.
     * If not, this method will throw an exception.
     * @param request http.request
     * @param delDbUser ApplicationUserEntity deleteUser
     * @throws Exception handle exception
     */
    private void checkRequestUserIsAuthorizedToDeleteAdminUser(HttpServletRequest request, ApplicationUserEntity delDbUser) throws Exception {
        var requestUser = serviceValidator.getValidDbUserFromRequest(request, userRepository);
        validateManipulationDbUserIsRequestUser(delDbUser, requestUser);
    }

    /**
     * Compares manipulationDbUser and requestUser by their ids.
     * If the ids are matching: nothing happens.
     * If not: Tt throws an AuthorizationServiceException with a custom ErrorMessage.
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param requestUser ApplicationUserEntity requestUser
     */
    private void validateManipulationDbUserIsRequestUser(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity requestUser) {
        serviceValidator.validateEqualUserIds(
            manipulationDbUser,
            requestUser,
            "You can't manipulate a admin-user, if you aren't this User!"
        );
    }

    /**
     * This method will be called, if the user, which want to be manipulated, has the admin-role.
     * Checks, that the role has not changed. You can't change the role of an admin-user!
     * @param manipulationDbUser ApplicationUserEntity manipulationDbUser
     * @param updatedUser ApplicationUserEntity updatedUser
     * @throws Exception handle exception
     */
    private void validateManipulationUserHasNotChangedRole(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        if(!manipulationDbUser.getRole().equals(updatedUser.getRole()) && updatedUser.getRole() != null)
            throw new Exception("You can't change the role of a admin-user! Admin-Role can't be removed.");
    }

    /**
     * Validates a passed role, whether the role matches the ApplicationRoles.
     * @param role String
     * @throws Exception handle exception
     */
    private void validateRole(String role) throws Exception {
        for(Role validRole : Role.values()) {
            if(role.equals(validRole.name())) return;
        }
        throw new Exception("The entered role is not valid! Please enter a valid role.");
    }

    /**
     * Validates, that the username has a size of min 5 and max 49
     * (needs to disable @Valid, because the password can be null by updating)
     * @param username String
     * @throws Exception handle exception
     */
    private void validateUsername(String username) throws Exception {
        if(username.length() < 4 || username.length() > 50)
            throw new Exception("Username needs a length of min 4 and max 50 characters!");
    }

    /**
     * Validates, that the password has a size of min 4 and max 300
     * @param password String
     * @throws Exception handle exception
     */
    private void validatePassword(String password) throws Exception {
        if(password.length() < 3 || password.length() > 300)
            throw new Exception("Password needs a length of min 3 and max 50 characters!");
    }
}
