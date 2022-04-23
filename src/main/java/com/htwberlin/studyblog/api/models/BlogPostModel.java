package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Date;

/** BlogPostModel
 *  Model for BlogPosts with title and content validation.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlogPostModel {
    private Long id;
    @Size(min = 4, max = 255, message = "Please enter a title with a size of min 4 and max 255 chars")
    private String title;

    @Size(min = 4, max = 3000, message = "Please enter a content with a size of min 4 and max 3000 chars")
    private String content;
    private Date creationDate;
    private Date lastEditDate;
    private Long creatorId;
    private String username;
    private boolean isFavorite;
}
