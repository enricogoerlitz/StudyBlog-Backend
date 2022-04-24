package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FavoritesModel
 *  Model for UserBlogPostFavorites.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritesModel {
    private Long id;
    private ApplicationUserModel creator;
    private Long blogPostId;
}
