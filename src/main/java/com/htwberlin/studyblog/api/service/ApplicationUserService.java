package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.PathVariableParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public ApplicationUserEntity updateUser(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity updatedUser) throws Exception {
        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthorizationServiceException("No valid JWT!");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null) throw new AuthorizationServiceException("User not found in DB!");

        if(updatedUser.getId() != dbUser.getId())
            throw new AuthorizationServiceException("User is not allowed to change the data of this user");

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
        refreshUserToken(request, response, savedUpdatedUser);

        return savedUpdatedUser; //userRepository.save(dbUser);
    }

    public ApplicationUserEntity updateUserByAdmin(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity updatedUser) throws Exception {
        var dbUser = userRepository.findById(updatedUser.getId());
        if(dbUser.isEmpty()) throw new AuthorizationServiceException("User not found in DB!");
        var verifiedDbUser = dbUser.get();

        if(!verifiedDbUser.getUsername().equals(updatedUser.getUsername())) {
            var isUsernameExisting = userRepository.findByUsername(updatedUser.getUsername());
            if(isUsernameExisting != null)
                throw new IllegalArgumentException("This Username is still existing");
            verifiedDbUser.setUsername(updatedUser.getUsername());
        }

        // TODO: validate PW and Role
        verifiedDbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        verifiedDbUser.setRole(updatedUser.getRole());

        var savedUpdatedUser = userRepository.save(verifiedDbUser);
        if(savedUpdatedUser == null) throw new Exception("User could not be updated!");
        refreshUserToken(request, response, savedUpdatedUser);

        return savedUpdatedUser;
    }

    public void deleteUser(String id) {
        Long userId = PathVariableParser.parseLong(id);
        var isUserExisting = userRepository.findById(userId);
        if(isUserExisting.isEmpty())
            throw new IllegalArgumentException("Could not delete the user. User was not found in the DB");

        favoriteRepository.deleteAllByCreator_Id(userId);
        blogPostRepository.deleteAllByCreator_Id(userId);
        userRepository.deleteById(userId);
    }

    private void refreshUserToken(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity user) throws IOException {
        log.warn("Here new refresh!");
        ApplicationJWT.refreshJWTCookie(request, response, user);
        /*
        var authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        var authentication = authenticationManager.authenticate(authToken);
        ApplicationJWT.refreshJWTCookie(request, response, authentication);

         */
    }
}
