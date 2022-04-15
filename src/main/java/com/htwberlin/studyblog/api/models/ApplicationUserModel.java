package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationUserModel {
    private Long id;
    private String username;
    private String password;
    private String role;
}
