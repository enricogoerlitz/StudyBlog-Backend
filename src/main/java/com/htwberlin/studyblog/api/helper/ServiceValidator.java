package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import com.htwberlin.studyblog.api.utilities.ResponseEntityException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

public final class ServiceValidator {
    public static ApplicationUserModel getValidRequestUser(HttpServletRequest request) throws Exception {
        return (ApplicationUserModel) getValidObjOrThrowException(
            ApplicationJWT.getUserFromJWT(request),
            AUTHORIZATION_SERVICE_EXCEPTION,
            "JWT-Token was not valid!"
        );
    }

    public static ApplicationUserEntity getValidDbUserById(ApplicationUserRepository userRepository, Long id) throws Exception {
        var optionalUser = userRepository.findById(id);
        return (ApplicationUserEntity) getValidObjOrThrowException(
            optionalUser.isEmpty() ? null : optionalUser.get(),
            ILLEGAL_ARGUMENT_EXCEPTION,
            "Could not find user with id " + id + " in the DB!"
        );
    }

    public static ApplicationUserEntity getValidDbUserByUsername(ApplicationUserRepository userRepository, String username) throws Exception {
        return (ApplicationUserEntity) getValidObjOrThrowException(
            userRepository.findByUsername(username),
            USERNAME_NOT_FOUND_EXCEPTION,
            "Could not find user with username " + username + " in the DB!"
        );
    }

    public static ApplicationUserEntity getValidDbUserFromRequest(HttpServletRequest request, ApplicationUserRepository userRepository) throws Exception {
        var requestUser = getValidRequestUser(request);
        return getValidDbUserByUsername(userRepository, requestUser.getUsername());
    }

    public static List<FavoritesEntity> getValidUserFavoriteBlogPostsByRequest(HttpServletRequest request, FavoriteRepository favoriteRepository) throws Exception {
        var user = getValidRequestUser(request);
        var userFavorites = favoriteRepository.findAllByCreator_Username(user.getUsername());
        return userFavorites == null ? new ArrayList<>() : userFavorites;
    }

    public static FavoritesEntity getValidFavoriteByBlogPostIdAndRequestUser(HttpServletRequest request, ApplicationUserRepository userRepository, FavoriteRepository favoriteRepository, Long blogPostId) throws Exception {
        var requestUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);

        return (FavoritesEntity) getValidObjOrThrowException(
                favoriteRepository.findByBlogPost_IdAndCreator_Id(blogPostId, requestUser.getId()),
                ILLEGAL_ARGUMENT_EXCEPTION,
                "Could not find the favorite of this user to the blogpost in the DB!"
        );
    }

    public static Set<Long> getValidUserFavoriteBlogPostsByRequestAsSet(HttpServletRequest request, FavoriteRepository favoriteRepository) throws Exception {
        return getValidUserFavoriteBlogPostsByRequest(request, favoriteRepository).stream().map(fav -> fav.getBlogPost().getId()).collect(Collectors.toSet());
    }

    public static BlogPostEntity getValidBlogPostById(BlogPostRepository blogPostRepository, Long id) throws Exception {
        var optionalBlogPost = blogPostRepository.findById(id);

        return (BlogPostEntity) getValidObjOrThrowException(
            optionalBlogPost.isEmpty() ? null : optionalBlogPost.get(),
            EXCEPTION,
            "Blogpost could not be found in DB!"
        ) ;
    }

    public static Object getValidObjOrThrowException(Object obj, ResponseEntityException exception, String exceptionMessage) throws Exception {
        if(obj == null)
            throwException(exception, exceptionMessage);

        return obj;
    }

    public static void validateEqualUserIds(ApplicationUserEntity user1, ApplicationUserEntity user2, String exceptionMessage) {
        if(user1.getId() != user2.getId())
            throw new AuthorizationServiceException(exceptionMessage);
    }

    private static void throwException(ResponseEntityException exception, String exceptionMessage) throws Exception {
        switch (exception) {
            case USERNAME_NOT_FOUND_EXCEPTION:
                throw new UsernameNotFoundException(exceptionMessage);
            case AUTHENTICATION_EXCEPTION:
                throw new AuthenticationException(exceptionMessage);
            case AUTHORIZATION_SERVICE_EXCEPTION:
                throw new AuthorizationServiceException(exceptionMessage);
            case ILLEGAL_ARGUMENT_EXCEPTION:
                throw new IllegalArgumentException(exceptionMessage);
            case DUPLICATE_KEY_EXCEPTION:
                throw new DuplicateKeyException(exceptionMessage);
            case EXCEPTION:
            default:
                throw new Exception(exceptionMessage);
        }
    }
}
