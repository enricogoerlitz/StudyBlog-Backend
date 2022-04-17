package com.htwberlin.studyblog.api.modelsEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;

@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    @Size(min = 4, max = 50, message = "Please enter a username with a length of min 4 and max 50 chars")
    private String username;

    @Column(name = "password", nullable = false)
    @Size(min = 3, max = 300, message = "Please enter a password with a length of min 4 and max 300 chars")
    private String password;

    @Column(name = "role", nullable = false)
    private String role;
}
