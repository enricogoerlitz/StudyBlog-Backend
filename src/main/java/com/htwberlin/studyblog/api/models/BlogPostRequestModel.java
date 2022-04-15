package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class BlogPostRequestModel {
    private Long id;
    private String title;
    private String content;
    private Date creationDate;
    private Date lastEditDate;
    private Long creatorId;
}
