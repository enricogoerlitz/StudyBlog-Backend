package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsentity.ApplicationUserEntity;

import java.util.Optional;

public final class Transformer {
    public static ApplicationUserModel userEntityToModel(Optional<ApplicationUserEntity> userEntity) {
        if(userEntity.isEmpty()) return null;
        return userEntityToModel(userEntity.get());
    }
    public static ApplicationUserModel userEntityToModel(ApplicationUserEntity userEntity) {
        if(userEntity == null) return null;
        return new ApplicationUserModel(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getPassword(),
            userEntity.getRole()
        );
    }
}
