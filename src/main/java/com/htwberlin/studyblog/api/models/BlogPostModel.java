package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
public class BlogPostModel {
    private Long id;
    private String title;
    private String content;
    private Date creationDate;
    private Date lastEditDate;
    private Long creatorId;
    private List<FavoritesModel> favourites;
}
