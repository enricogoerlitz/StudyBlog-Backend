package com.htwberlin.studyblog.api.modelsEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/** FavoritesEntity
 *  EntityTable for UserBlogPostFavorites.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "favourites")
public class FavoritesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private ApplicationUserEntity creator;

    @ManyToOne
    @JoinColumn(name = "blog_post_id", referencedColumnName = "id")
    private BlogPostEntity blogPost;
}
