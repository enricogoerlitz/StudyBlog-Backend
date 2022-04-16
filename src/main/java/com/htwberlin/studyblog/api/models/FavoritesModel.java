package com.htwberlin.studyblog.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FavoritesModel {
    private Long id;
    private Long creatorId;
    private Long blogPostId;

}
