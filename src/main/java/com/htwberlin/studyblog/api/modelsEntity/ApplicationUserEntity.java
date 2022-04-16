package com.htwberlin.studyblog.api.modelsEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** TODO: implement a Many-to-Many relationship to ApplicationUserEntity and FavouriteEntity
 *
 */
@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;
}
