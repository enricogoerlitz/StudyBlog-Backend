package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavouriteEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FavouriteService {
    private final FavouriteRepository favouritesRepository;
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;

    public List<FavouriteEntity> getFavourites() {
        return favouritesRepository.findAll();
    }

    public List<FavouriteEntity> getFavouritesByCreator(Long id) {
        return favouritesRepository.findAllByCreator_Id(id);
    }

    public FavouriteEntity addFavourite(Long creatorId, Long blogPostId) {
        var user = userRepository.findById(creatorId);
        var blogPost = blogPostRepository.findById(blogPostId);
        if(user == null || blogPost == null) return null;

        var addedFavourite = favouritesRepository.save(new FavouriteEntity(null, user.get(), blogPost.get()));

        return addedFavourite;
    }

    public FavouriteEntity addFavourite(ApplicationUserEntity creator, BlogPostEntity blogPost) {
        if(creator == null || blogPost == null) return null;
        var addedFavourite = favouritesRepository.save(new FavouriteEntity(null, creator, blogPost));

        return addedFavourite;
    }
}
