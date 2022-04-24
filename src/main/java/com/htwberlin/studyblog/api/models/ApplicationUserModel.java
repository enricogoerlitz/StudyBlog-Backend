package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** ApplicationUserModel
 *  Model for ApplicationUsers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUserModel {
    private Long id;
    private String username;
    private String role;
}
