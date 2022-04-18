package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/** ApplicationUserModel
 *  Model for ApplicationUsers.
 */
@Data
@AllArgsConstructor
public class ApplicationUserModel {
    private Long id;
    private String username;
    private String role;
}
