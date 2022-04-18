package com.htwberlin.studyblog.api.helper;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.repository.ApplicationUserRepository;
import com.htwberlin.studyblog.api.repository.BlogPostRepository;
import com.htwberlin.studyblog.api.repository.FavoriteRepository;
import org.springframework.security.access.AuthorizationServiceException;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/** ServiceValidator
 * Static Class for Services to get valid Instances of Users, BlogPosts and BlogPost-Favorites
 * or throwing an exception if
 *      (1.) the JWT-Token is not valid,
 *      (2.) the resource could not be found in the DB
 *      (3.) the valid User is not authorized to get this resource
 */
public final class ServiceValidator {

    /**
     * Returns a valid ApplicationUserModel, based on the JWT-Token of the request
     * or throws an exception.
     * (1.) Tries to get the user from the Cookie, validates the Cookie-JWT, if is valid, returns the valid UserModel or
     * (2.) Tries to get the user from the Authorization-Header, validates the Cookie-JWT, if is valid,
     *      returns the valid UserModel or Null
     * if the result is null, this method throws an exception
     * @param request http.request
     * @return ApplicationUserModel valid UserModel (WITHOUT ID)
     * @throws Exception handling exception
     */
    public static ApplicationUserModel getValidRequestUser(HttpServletRequest request) throws Exception {
        return ObjectValidator.getValidObjOrThrowException(
            ApplicationJWT.getUserFromJWT(request),
            AUTHORIZATION_SERVICE_EXCEPTION,
            "JWT-Token was not valid!"
        );
    }

    /**
     * Returns a valid ApplicationUserEntity, based on the JWT-Token of the request
     * or throws an exception.
     * (1.) Tries to get the user from the Cookie, validates the Cookie-JWT, if is valid
     *      it tries to fetch the user by username from the DB.
     *      If the result is not null, the valid DB-ApplicationUserEntity will be returned.
     * (2.) Tries to get the user from the Authorization-Header, validates the Cookie-JWT, if is valid
     *      it tries to fetch the user by username from the DB.
     *      If the result is not null, the valid DB-ApplicationUserEntity will be returned.
     * If the result is null, this method throws an exception.
     * @param request http.request
     * @param userRepository ApplicationUserRepository
     * @return ApplicationUserEntity valid DbUser
     * @throws Exception handling exception
     */
    public static ApplicationUserEntity getValidDbUserFromRequest(HttpServletRequest request, ApplicationUserRepository userRepository) throws Exception {
        var requestUser = getValidRequestUser(request);
        return getValidDbUserByUsername(userRepository, requestUser.getUsername());
    }

    /**
     * Returns a valid ApplicationUserEntity, based in the id
     * or throws an exception.
     * It tries to fetch the user by id from the DB.
     * If the result is not null, the valid DB-ApplicationUserEntity will be returned.
     * If the result is null, this method throws an exception.
     * @param id userId
     * @param userRepository ApplicationUserRepository
     * @return ApplicationUserEntity valid DbUser
     * @throws Exception handling exception
     */
    public static ApplicationUserEntity getValidDbUserById(ApplicationUserRepository userRepository, Long id) throws Exception {
        var optionalUser = userRepository.findById(id);
        return ObjectValidator.getValidObjOrThrowException(
            optionalUser.isEmpty() ? null : optionalUser.get(),
            ILLEGAL_ARGUMENT_EXCEPTION,
            "Could not find user with id " + id + " in the DB!"
        );
    }

    /**
     * Returns a valid ApplicationUserEntity, based in the username
     * or throws an exception.
     * It tries to fetch the user by username from the DB.
     * If the result is not null, the valid DB-ApplicationUserEntity will be returned.
     * If the result is null, this method throws an exception.
     * @param username username
     * @param userRepository ApplicationUserRepository
     * @return ApplicationUserEntity valid DbUser
     * @throws Exception handling exception
     */
    public static ApplicationUserEntity getValidDbUserByUsername(ApplicationUserRepository userRepository, String username) throws Exception {
        return ObjectValidator.getValidObjOrThrowException(
            userRepository.findByUsername(username),
            USERNAME_NOT_FOUND_EXCEPTION,
            "Could not find user with username " + username + " in the DB!"
        );
    }

    /**
     * Returns a valid BlogPostEntity, based in the id
     * or throws an exception.
     * It tries to fetch the blogpost by id from the DB.
     * If the result is not null, the valid DB-BlogPostEntity will be returned.
     * If the result is null, this method throws an exception.
     * @param id blogPostId
     * @param blogPostRepository BlogPostRepository
     * @return BlogPostEntity valid DbBlogPost
     * @throws Exception handling exception
     */
    public static BlogPostEntity getValidBlogPostById(BlogPostRepository blogPostRepository, Long id) throws Exception {
        var optionalBlogPost = blogPostRepository.findById(id);
        return ObjectValidator.getValidObjOrThrowException(
                optionalBlogPost.isEmpty() ? null : optionalBlogPost.get(),
                EXCEPTION,
                "Blogpost could not be found in DB!"
        ) ;
    }

    /**
     * Returns a list of FavoritesEntities, based on the JWT-Token request-user
     * or throws an exception.
     * Tries to get the valid request-user by JWT-Token.
     * Tries to fetch all favorites of the user.
     * If the result is not null, this list will be returned.
     * If the result is null, an empty ArrayList will be returned.
     * @param request http.request
     * @param favoriteRepository FavoriteRepository
     * @return List<FavoritesEntity>
     * @throws Exception handling exception
     */
    public static List<FavoritesEntity> getValidUserFavoriteBlogPostsByRequest(HttpServletRequest request, FavoriteRepository favoriteRepository) throws Exception {
        var user = getValidRequestUser(request);
        var userFavorites = favoriteRepository.findAllByCreator_Username(user.getUsername());
        return userFavorites == null ? new ArrayList<>() : userFavorites;
    }

    /**
     * Returns a single FavoritesEntity, based on the JWT-Token request-user
     * or throws an exception.
     * Tries to get the valid dbUser by JWT-Token.
     * Tries to fetch the favorite from the DB, based on the blogPostId and the creatorId (request-user).
     * If the result is not null, this single FavoritesEntity will be returned.
     * If the result is null, this method throws an exception.
     * @param request http.request
     * @param userRepository ApplicationUserRepository
     * @param favoriteRepository FavoriteRepository
     * @param blogPostId blogPostId
     * @return FavoritesEntity single fetched favorite
     * @throws Exception handling exception
     */
    public static FavoritesEntity getValidFavoriteByBlogPostIdAndRequestUser(HttpServletRequest request, ApplicationUserRepository userRepository, FavoriteRepository favoriteRepository, Long blogPostId) throws Exception {
        var requestUser = ServiceValidator.getValidDbUserFromRequest(request, userRepository);
        return ObjectValidator.getValidObjOrThrowException(
                favoriteRepository.findByBlogPost_IdAndCreator_Id(blogPostId, requestUser.getId()),
                ILLEGAL_ARGUMENT_EXCEPTION,
                "Could not find the favorite of this user to the blogpost in the DB!"
        );
    }

    /**
     * Returns a Set of BlogPostIds, witch match with the favorites of the request-user.
     * @param request http.request
     * @param favoriteRepository FavoriteRepository
     * @return Set<Long> a Set of BlogPostIds
     * @throws Exception handling exception
     */
    public static Set<Long> getValidUserFavoriteBlogPostIdsByRequestAsSet(HttpServletRequest request, FavoriteRepository favoriteRepository) throws Exception {
        return getValidUserFavoriteBlogPostsByRequest(request, favoriteRepository)
                .stream().map(fav -> fav.getBlogPost().getId()).collect(Collectors.toSet());
    }

    /**
     * Compares two users by their ids.
     * If the ids are matching: nothing happens.
     * If not: Tt throws an AuthorizationServiceException with a custom ErrorMessage.
     * @param user1 ApplicationUserEntity first user
     * @param user2 ApplicationUserEntity second user
     * @param exceptionMessage String custom ErrorMessage
     */
    public static void validateEqualUserIds(ApplicationUserEntity user1, ApplicationUserEntity user2, String exceptionMessage) {
        if(user1.getId() != user2.getId())
            throw new AuthorizationServiceException(exceptionMessage);
    }
}
