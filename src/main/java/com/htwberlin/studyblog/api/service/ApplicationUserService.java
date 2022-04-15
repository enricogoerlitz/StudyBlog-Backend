package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final ApplicationUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByUsername(username);
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

    public ApplicationUserModel registerUser(ApplicationUserModel user) {
        log.info("Saving new user to the db.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var entityUser = Transformer.userModelToEntity(user);
        var registeredUser = repository.save(entityUser);
        return Transformer.userEntityToModel(registeredUser);
    }

    /*
    ApplicationUserModel saveUser(ApplicationUserEntity user) {
        var savedUser = repository.save(user);
        return Transformer.userEntityToModel(savedUser);
    }
    */

    public ApplicationUserModel getUser(Long id) {
        log.info("fetching user from the db.");
        var user = repository.findById(id);
        return Transformer.userEntityToModel(user);
    }

    public ApplicationUserModel getUser(String username) {
        log.info("fetching user from the db.");
        var user = repository.findByUsername(username);
        return Transformer.userEntityToModel(user);
    }

    public List<ApplicationUserModel> getUsers() {
        log.info("fetching all users from the db.");
        var users = repository.findAll();
        return users.stream().map(entity -> Transformer.userEntityToModel(entity)).toList();
    }
}
