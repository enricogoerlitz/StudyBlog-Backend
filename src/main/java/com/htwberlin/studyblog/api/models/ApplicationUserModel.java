package com.htwberlin.studyblog.api.models;

import com.htwberlin.studyblog.api.authentication.JWTUser;

/** ApplicationUserModel
 *  Model for ApplicationUsers.
 */
public class ApplicationUserModel extends JWTUser {
    private Long id;

    public ApplicationUserModel(Long id, String username, String role) {
        super(username, role);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
