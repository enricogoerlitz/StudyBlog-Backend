package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplicationUserService implements UserDetailsService {
    private final ApplicationUserRepository userRepository;
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
            Arrays.asList(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    public ApplicationUserEntity registerUser(ApplicationUserEntity user) {
        log.info("Saving new user to the db.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var registeredUser = userRepository.save(user);

        return registeredUser;
    }

    public ApplicationUserEntity getUser(Long id) {
        log.info("fetching user from the db.");
        var user = userRepository.findById(id);
        if(user == null) return null;
        return user.get();
    }

    public ApplicationUserEntity getUser(String username) {
        log.info("fetching user from the db.");
        var user = userRepository.findByUsername(username);

        return user;
    }

    public List<ApplicationUserEntity> getUsers() {
        log.info("fetching all users from the db.");
        var users = userRepository.findAll();
        return users; // users.stream().map(entity -> Transformer.userEntityToModel(entity)).toList();
    }

    // TODO: implement delete user
    public void deleteUser() {
        throw new RuntimeException();
    }

    // TODO: implement update user
    public ApplicationUserModel updateUser(ApplicationUserModel updatedUser) {
        throw new RuntimeException();
    }
}
