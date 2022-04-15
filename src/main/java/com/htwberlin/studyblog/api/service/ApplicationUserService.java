package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.helper.Transformer;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsentity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.rmi.server.LogStream.log;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplicationUserService {
    private final ApplicationUserRepository repository;

    ApplicationUserModel registerUser(ApplicationUserEntity user) {
        log.info("Saving new user to the db.");
        var registeredUser = repository.save(user);
        return Transformer.userEntityToModel(registeredUser);
    }

    /*
    ApplicationUserModel saveUser(ApplicationUserEntity user) {
        var savedUser = repository.save(user);
        return Transformer.userEntityToModel(savedUser);
    }
    */

    ApplicationUserModel getUser(Long id) {
        log.info("fetching user from the db.");
        var user = repository.findById(id);
        return Transformer.userEntityToModel(user);
    }

    ApplicationUserModel getUser(String username) {
        log.info("fetching user from the db.");
        var user = repository.findByUsername(username);
        return Transformer.userEntityToModel(user);
    }

    List<ApplicationUserModel> getUsers() {
        log.info("fetching all users from the db.");
        var users = repository.findAll();
        return users.stream().map(entity -> Transformer.userEntityToModel(entity)).toList();
    }
}
