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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Overrides a method fpr user-authentication. By request to /api/v1/login
     * the authentication-process gets triggered and trys to find a user in the DB by username.
     * If the method can find a user by his username in the DB, its return an auth-user
     * otherwise it throws an error (UsernameNotFoundException).
     * @param username username of the post-param 'username'
     * @return UserDetails
     * @throws UsernameNotFoundException if user could not be found by username, throw this exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var existingUser = userRepository.findByUsername(username);
        if(existingUser == null) {
            log.error("User with username {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }

        return new User(
            existingUser.getUsername(),
            existingUser.getPassword(),
            List.of(new SimpleGrantedAuthority(existingUser.getRole()))
        );
    }

    /**
     * Fetches all users from the DB and transforms the entities to models.
     * @return List<ApplicationUserModel>
     */
    public List<ApplicationUserModel> getUsers() {
        return EntityModelTransformer.userEntitiesToModels(userRepository.findAll());
    }

    /** registerUser
     * Saves a new User with an initial role to the DB. This method is for Users, who are registering themselves.
     * @param user ApplicationUserEntity The userdata, which should be registered
     * @param initialRole String The role, what the user should get
     * @return ApplicationUserModel
     */
    public ApplicationUserModel registerUser(ApplicationUserEntity user, String initialRole) throws Exception {
        user.setRole(initialRole);
        return registerUser(user);
    }

    /** registerUser
     * Saves a new User with an encrypted password to the DB. This method is for Admins, who register a new User.
     * @param user The userdata, which should be registered (by admin, incl. role)
     * @return ApplicationUserModel
     */
    public ApplicationUserModel registerUser(ApplicationUserEntity user) throws Exception {
        validateRole(user.getRole());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return EntityModelTransformer.userEntityToModel(userRepository.save(user));
    }

    /** updateUser
     * Updates the logged user (from JWT-Token)
     * It updates the username and password, if they have changed.
     * MORE!
     *
     *
     * @param request http.request
     * @param response http.response
     * @param updatedUser the userdata, which should updated to the DB.
     * @return ApplicationUserModel
     * @throws Exception handling exception
     */
    public ApplicationUserModel updateUser(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity updatedUser) throws Exception {
        var manipulationDbUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        validateRole(manipulationDbUser.getRole());
        updatedUser.setId(manipulationDbUser.getId());
        updatedUser.setRole(manipulationDbUser.getRole());
        changeUsername(manipulationDbUser, updatedUser);
        changePassword(manipulationDbUser, updatedUser);

        var savedUpdatedUser = ObjectValidator.getValidObjOrThrowException(
            userRepository.save(manipulationDbUser),
            EXCEPTION,
            "User could not be updated!"
        );

        ApplicationJWT.refreshJWTCookie(request, response, savedUpdatedUser);

        return EntityModelTransformer.userEntityToModel(savedUpdatedUser);
    }

    /**
     * Description:
     * Updating the username, password and role of a user (by userId)
     * admins can modify students and themselves
     * admins can't modify other admin, only themselves
     * admins can modify themselves, but can't remove/change there role to a student, visitor oder any other role
     *
     * @param request http.request
     * @param id id of the user, which should be updated by an admin
     * @param updatedUser the userdata, which should updated to the DB.
     * @return ApplicationUserEntity
     * @throws Exception handling exception
     */
    public ApplicationUserModel updateUserByAdmin(HttpServletRequest request, String id, ApplicationUserEntity updatedUser) throws Exception {
        Long dbUserId = PathVariableParser.parseLong(id);
        var manipulationDbUser = ServiceValidator.getValidDbUserById(userRepository, dbUserId);

        if(isManipulationUserAdmin(manipulationDbUser))
            checkRequestUserIsAuthorizedForAdminManipulation(request, manipulationDbUser, updatedUser);

        changeUsername(manipulationDbUser, updatedUser);
        changePassword(manipulationDbUser, updatedUser);
        changeRole(manipulationDbUser, updatedUser);

        return EntityModelTransformer.userEntityToModel(userRepository.save(manipulationDbUser));
    }

    /**
     * Admins can delete users, incl. themselves
     * But Admins can't delete other admin-user
     * Deletes all references to this userId in the DB
     *
     * @param request http.request
     * @param id id of the user, which should be deleted
     * @throws Exception handling exception
     */
    public void deleteUser(HttpServletRequest request, String id) throws Exception {
        Long userId = PathVariableParser.parseLong(id);
        var delDbUser = ServiceValidator.getValidDbUserById(userRepository, userId);

        if(isManipulationUserAdmin(delDbUser))
            checkRequestUserIsAuthorizedToDeleteAdminUser(request, delDbUser);

        favoriteRepository.deleteAllByCreator_Id(userId);
        blogPostRepository.deleteAllByCreator_Id(userId);
        userRepository.deleteById(userId);
    }

    private void changeUsername(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) {
        if(manipulationDbUser.getUsername().equals(updatedUser.getUsername())) return;

        var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
        if(isUsernameExisting != null)
            throw new IllegalArgumentException("This Username is still existing!");

        manipulationDbUser.setUsername(updatedUser.getUsername());
    }

    private void changePassword(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) {
        manipulationDbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }

    private void changeRole(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        if(updatedUser.getRole() == null) return;
        validateRole(updatedUser.getRole());
        manipulationDbUser.setRole(updatedUser.getRole());
    }

    private boolean isManipulationUserAdmin(ApplicationUserEntity user) {
        return user.getRole().equals(Role.ADMIN.name());
    }

    private void checkRequestUserIsAuthorizedForAdminManipulation(HttpServletRequest request, ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        var requestUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        validateManipulationDbUserIsRequestUser(manipulationDbUser, requestUser);
        validateManipulationUserHasNotChangedRole(manipulationDbUser, updatedUser);
    }

    private void checkRequestUserIsAuthorizedToDeleteAdminUser(HttpServletRequest request, ApplicationUserEntity delDbUser) throws Exception {
        var requestUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        validateManipulationDbUserIsRequestUser(delDbUser, requestUser);
    }

    private void validateManipulationDbUserIsRequestUser(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity requestUser) {
        ServiceValidator.validateEqualUserIds(
                manipulationDbUser,
                requestUser,
                "You can't manipulate a admin-user, if you aren't this User!"
        );
    }

    private void validateManipulationUserHasNotChangedRole(ApplicationUserEntity manipulationDbUser, ApplicationUserEntity updatedUser) throws Exception {
        if(!manipulationDbUser.getRole().equals(updatedUser.getRole()) && updatedUser.getRole() != null)
            throw new Exception("You can't change the role of a admin-user! Admin-Role can't be removed.");
    }

    private void validateRole(String role) throws Exception {
        for(Role validRole : Role.values()) {
            if(role.equals(validRole.name())) return;
        }
        throw new Exception("The entered role is not valid! Please enter a valid role.");
    }
}
