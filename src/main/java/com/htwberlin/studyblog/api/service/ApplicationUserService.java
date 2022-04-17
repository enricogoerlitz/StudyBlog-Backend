package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.PathVariableParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.jdbc.PreferQueryMode;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import java.io.IOException;
import java.util.List;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);
        if(user == null) {
            log.error("User with username {} not found", username);
            throw new UsernameNotFoundException("User not found");
        }
        log.info("User with username {} found", username);

        return new User(
            user.getUsername(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public ApplicationUserEntity registerUser(ApplicationUserEntity user) {
        log.info("Saving new user to the db.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public ApplicationUserEntity registerUser(ApplicationUserEntity user, String initialRole) {
        user.setRole(initialRole);
        return registerUser(user);
    }


    public ApplicationUserEntity getUser(Long id) {
        log.info("fetching user from the db.");
        var user = userRepository.findById(id);
        if(user.isEmpty()) return null;
        return user.get();
    }

    public ApplicationUserEntity getUser(String username) {
        log.info("fetching user from the db.");
        return userRepository.findByUsername(username);
    }

    public List<ApplicationUserEntity> getUsers() {
        log.info("fetching all users from the db.");
        return userRepository.findAll();
    }

    /**
     * edit the logged user => username and password
     * @param request
     * @param response
     * @param updatedUser
     * @return
     * @throws Exception
     */
    public ApplicationUserEntity updateUser(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity updatedUser) throws Exception {
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthorizationServiceException("No valid JWT!");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null) throw new AuthorizationServiceException("User not found in DB!");

        /*
        if(updatedUser.getId() != dbUser.getId())
            throw new AuthorizationServiceException("User is not allowed to change the data of this user");

         */
        updatedUser.setId(requestUser.getId());
        updatedUser.setRole(requestUser.getRole());

        if(!dbUser.getUsername().equals(updatedUser.getUsername())) {
            var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
            if(isUsernameExisting != null)
                throw new IllegalArgumentException("This Username is still existing");

            // TODO: validate username
            dbUser.setUsername(updatedUser.getUsername());
        }
        // TODO: validate PW
        dbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        var savedUpdatedUser = userRepository.save(dbUser);
        if(savedUpdatedUser == null) throw new Exception("User could not be updated!");

        ApplicationJWT.refreshJWTCookie(request, response, savedUpdatedUser);

        return savedUpdatedUser;
    }

    /**
     * edit the username, password and role of a sendet user (id)
     * admins can modify students
     * admins can't modify other admin, only them self
     *
     * @param request
     * @param response
     * @param id
     * @param updatedUser
     * @return ApplicationUserEntity
     * @throws Exception
     */
    public ApplicationUserEntity updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, String id, ApplicationUserEntity updatedUser) throws Exception {
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

        return userRepository.save(verifiedDbUser);
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
}
