package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/** TODO: implement the BlogPostModel and add the entity for this
 *
 */
@Data
@AllArgsConstructor
public class BlogPostModel {
    private Long id;
    private String title;
    private String content;
    private Date creationDate;
    private Date lastEditDate;
    private ApplicationUserModel creator;
}
