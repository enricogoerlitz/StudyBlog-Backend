package com.htwberlin.studyblog.api.service;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.PathVariableParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FavoritesService {
    private final FavoriteRepository favouritesRepository;
    private final ApplicationUserRepository userRepository;
    private final BlogPostRepository blogPostRepository;

    public List<FavoritesModel> getFavoritesByCreator(HttpServletRequest request) throws AuthenticationException {
        // TODO: source out
        var validUser = ApplicationJWT.getUserFromJWT(request);
        if(validUser == null) throw new AuthenticationException("User has no valid JWT");

        var dbFavorites = favouritesRepository.findAllByCreator_Username(validUser.getUsername());
        if(dbFavorites == null || dbFavorites.size() <= 0) return new ArrayList<>();

        return dbFavorites.stream().map(fav -> new FavoritesModel(fav.getId(), fav.getCreator().getId(), fav.getBlogPost().getId())).toList();
    }

    public FavoritesModel addFavourite(ApplicationUserEntity creator, BlogPostEntity blogPost) {
        if(creator == null || blogPost == null) return null;
        var entity = new FavoritesEntity(null, creator, blogPost);

        var addedEntity = favouritesRepository.save(entity);

        // TODO to Transformer-Class
        return new FavoritesModel(addedEntity.getId(), addedEntity.getCreator().getId(), addedEntity.getBlogPost().getId());
    }

    // TODO: source out AuthenticationException to utilities
    public FavoritesModel addFavourite(HttpServletRequest request, String blogPostId) throws AuthenticationException {
        var validBlogPostId = PathVariableParser.parseLong(blogPostId);

        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthenticationException("User has no valid JWT");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null)
            throw new AuthenticationException("Unknown user. User not found in DB");

        var blogPost = blogPostRepository.findById(validBlogPostId);
        if(blogPost.isEmpty()) return null;

// TODO: findByCreatorIdAnd
        var isStillExisting = favouritesRepository.findByBlogPost_IdAndCreator_Id(blogPost.get().getId(), dbUser.getId());
        if(isStillExisting != null) throw new DuplicateKeyException("This blog is already a favorite!");
        var addedEntity = favouritesRepository.save(new FavoritesEntity(null, dbUser, blogPost.get()));

        // TODO to Transformer-Class
        return new FavoritesModel(addedEntity.getId(), addedEntity.getCreator().getId(), addedEntity.getBlogPost().getId());
    }

    public void removeFavorite(HttpServletRequest request, String favoriteId) throws Exception {
        var validFavoriteId = PathVariableParser.parseLong(favoriteId);

        var requestUser = ApplicationJWT.getUserFromJWT(request);
        if(requestUser == null) throw new AuthenticationException("User has no valid JWT!");

        var dbUser = userRepository.findByUsername(requestUser.getUsername());
        if(dbUser == null) throw new AuthenticationException("User not found in DB!");

        var favorite = favouritesRepository.findById(validFavoriteId);

        if(favorite.isEmpty()) throw new Exception("Favorite not found");
        var validFavorite = favorite.get();
        if(validFavorite.getBlogPost().getCreator().getId() != dbUser.getId())
            throw new AuthorizationServiceException("User is not allowed to delete this favorite");

        favouritesRepository.deleteById(validFavoriteId);
    }
}
