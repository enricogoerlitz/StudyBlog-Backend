package com.htwberlin.studyblog.api.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTUser {
    private String username;
    private String role;
}
