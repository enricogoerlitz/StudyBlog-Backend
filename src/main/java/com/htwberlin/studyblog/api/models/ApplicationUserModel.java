package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApplicationUserModel {
    private long id;
    private String username;
    private String password;
    private String role;
}
