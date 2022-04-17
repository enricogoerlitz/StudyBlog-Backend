package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.helper.ServiceValidator;
import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.PathVariableParser;
import com.htwberlin.studyblog.api.utilities.ResponseEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
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

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.AUTHORIZATION_SERVICE_EXCEPTION;
import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.EXCEPTION;

/**
 * TODO: change return null to throw new Exception
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    /** loadUserByUsername
     * Overrides a method fpr user-authentication. By request to /api/v1/login
     * the authentication-process gets triggered and trys to find a user in the DB by username
     * If the method can find a user by his username in the DB, its return a auth-user
     * otherwise it throws an error (UsernameNotFoundException)
     *
     * @param username
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var existingUser = userRepository.findByUsername(username);
        if(existingUser == null) {
            log.error("User with username {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }

        log.info("User with username {} found!", username);
        return new User(
            existingUser.getUsername(),
            existingUser.getPassword(),
            List.of(new SimpleGrantedAuthority(existingUser.getRole()))
        );
    }

    public List<ApplicationUserModel> getUsers() {
        return Transformer.userEntitiesToModels(userRepository.findAll());
    }

    /** registerUser
     * Saves a new User to the DB. This method is for Users, who register themselves.
     * @param user
     * @param initialRole
     * @return ApplicationUserModel
     */
    public ApplicationUserModel registerUser(ApplicationUserEntity user, String initialRole) {
        user.setRole(initialRole);
        return registerUser(user);
    }

    /** registerUser
     * Saves a new User to the DB. This method is for Admins, who register a new User.
     * @param user
     * @return ApplicationUserModel
     */
    public ApplicationUserModel registerUser(ApplicationUserEntity user) {
        log.info("Saving new user to the db.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Transformer.userEntityToModel(userRepository.save(user));
    }

    /** updateUser
     * Updates the logged user (from JWT-Token)
     * It updates the username and password, if they have changed.
     *
     * @param request
     * @param response
     * @param updatedUser
     * @return ApplicationUserModel
     * @throws Exception
     */
    public ApplicationUserModel updateUser(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity updatedUser) throws Exception {
        // var requestUser = ServiceValidator.getValidRequestUser(request);
        // var dbUser = ServiceValidator.getValidDbUserByUsername(userRepository, requestUser.getUsername())
        /*
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthorizationServiceException("No valid JWT!");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null) throw new AuthorizationServiceException("User not found in DB!");


                 */
                /*
        updatedUser.setId(requestUser.getId());
        updatedUser.setRole(requestUser.getRole());
         */
                /*
        if(!dbUser.getUsername().equals(updatedUser.getUsername())) {
            var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
            if(isUsernameExisting != null)
                throw new IllegalArgumentException("This Username is still existing");

            // TODO: validate username
            dbUser.setUsername(updatedUser.getUsername());
        }

         */
                /*
        // TODO: validate PW
        dbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));


         */

                /*
        var savedUpdatedUser = userRepository.save(dbUser);
        if(savedUpdatedUser == null) throw new Exception("User could not be updated!");


                 */
        var dbUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        updatedUser.setId(dbUser.getId());
        updatedUser.setRole(dbUser.getRole());
        changeUsername(dbUser, updatedUser);
        changePassword(dbUser, updatedUser);

        var savedUpdatedUser = (ApplicationUserEntity)ServiceValidator.getValidObjOrThrowException(
            userRepository.save(dbUser),
            EXCEPTION,
            "User could not be updated!"
        );

        ApplicationJWT.refreshJWTCookie(request, response, savedUpdatedUser);

        return Transformer.userEntityToModel(savedUpdatedUser);
    }

    /**
     * edit the username, password and role of a sendet user (id)
     * admins can modify students
     * admins can't modify other admin, only themselves
     *
     * @param request
     * @param response
     * @param id
     * @param updatedUser
     * @return ApplicationUserEntity
     * @throws Exception
     */
    public ApplicationUserModel updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, String id, ApplicationUserEntity updatedUser) throws Exception {
        Long dbUserId = PathVariableParser.parseLong(id);
        var dbUser = userRepository.findById(dbUserId);
        if(dbUser.isEmpty()) throw new IllegalArgumentException("User not found in DB!");
        var verifiedDbUser = dbUser.get();

        // TODO: source out
        if(verifiedDbUser.getRole().equals(Role.ADMIN.name())) {
            var requestUser = ApplicationJWT.getUserFromJWT(request);
            if(requestUser == null) throw new AuthorizationServiceException("JWT is invalid! You can't manipulate a admin-user, if you are not this user!");

            var verifiedReqUser = userRepository.findByUsername(requestUser.getUsername());
            if(verifiedReqUser == null) throw new AuthorizationServiceException("User not found in DB. You can't manipulate a admin-user, if you are not this user!");

            if(verifiedDbUser.getId() != verifiedReqUser.getId())
                throw new AuthorizationServiceException("You can't manipulate a admin user, if you are't this User!");

            if(!verifiedDbUser.getRole().equals(verifiedReqUser.getRole()))
                throw new Exception("You can't change the role of a admin-user!");
        }

        // TODO: source out
        if(!verifiedDbUser.getUsername().equals(updatedUser.getUsername())) {
            var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
            if(isUsernameExisting != null)
                throw new IllegalArgumentException("This Username is still existing");
            verifiedDbUser.setUsername(updatedUser.getUsername());
        }

        // TODO: validate PW and Role
        verifiedDbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        verifiedDbUser.setRole(updatedUser.getRole());

        return Transformer.userEntityToModel(userRepository.save(verifiedDbUser));
    }

    public void deleteUser(HttpServletRequest request, String id) {
        Long userId = PathVariableParser.parseLong(id);
        var isUserExisting = userRepository.findById(userId);
        if(isUserExisting.isEmpty())
            throw new IllegalArgumentException("Could not delete the user. User was not found in the DB");

        if(isUserExisting.get().getRole().equals(Role.ADMIN.name())) {
            var requestUser = ApplicationJWT.getUserFromJWT(request);
            if(requestUser == null)
                throw new AuthorizationServiceException("JWT is invalid!");
            var dbUser = userRepository.findByUsername(requestUser.getUsername());
            if(dbUser == null)
                throw new AuthorizationServiceException("User not found in DB");

            if(dbUser.getId() != isUserExisting.get().getId())
                throw new AuthorizationServiceException("You can't delete this admin-user! If a user has the role 'Admin', this user is the only one how can delete this user!");
        }

        favoriteRepository.deleteAllByCreator_Id(userId);
        blogPostRepository.deleteAllByCreator_Id(userId);
        userRepository.deleteById(userId);
    }

    private void changeUsername(ApplicationUserEntity dbUser, ApplicationUserEntity updatedUser) {
        if(dbUser.getUsername().equals(updatedUser.getUsername())) return;
        var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
        if(isUsernameExisting != null)
            throw new IllegalArgumentException("This Username is still existing");

        // TODO: validate username
        dbUser.setUsername(updatedUser.getUsername());
    }

    private void changePassword(ApplicationUserEntity dbUser, ApplicationUserEntity updatedUser) {
        // TODO: validate password
        dbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }
}
