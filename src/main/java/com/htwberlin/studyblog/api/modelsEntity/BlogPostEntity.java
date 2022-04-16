package com.htwberlin.studyblog.api.modelsEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @OneToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private ApplicationUserEntity creator;

    @OneToMany(cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<FavoritesEntity> favourites;

    /*
    public BlogPostEntity(Long id, String title, String content, Date creationDate, Date lastEditDate, ApplicationUserEntity creator) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.lastEditDate = lastEditDate;
        this.creator = creator;
        this.favourites = new ArrayList<>();
    }

     */
}
