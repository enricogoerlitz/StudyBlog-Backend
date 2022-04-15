package com.htwberlin.studyblog.api.modelsEntity;

import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/** TODO: implement BlogPostEntity with a Many-to-Many relationship to ApplicationUserEntity and FavouriteEntity
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "posts")
public class BlogPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, length = 1500)
    private String content;

    @Column(name = "create_date", nullable = false)
    private Date creationDate;

    @Column(name = "last_edit_data")
    private Date lastEditDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private ApplicationUserEntity creator;
}
