package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.service.FavoritesService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/** FavoritesController
 *  RESTController for Favorites-Routes
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.API)
public class FavoritesController {
    private final FavoritesService favoritesService;

    @GetMapping(Routes.FAVORITES)
    public ResponseEntity<Set<Long>> getFavoritesByUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            var favorites = favoritesService.getFavoritesByCreator(request);
            return ResponseEntity.status(HttpStatus.OK).body(favorites);
        } catch (AuthenticationException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.FAVORITES_ID)
    public ResponseEntity<FavoritesModel> addFavoriteToUser(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            var addedFavorite = favoritesService.addFavorite(request, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedFavorite);
        } catch (AuthenticationException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (DuplicateKeyException exp) {
            return ResponseEntityExceptionManager.handleException(response, DUPLICATE_KEY_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @param id blogPostId
     * @return
     */
    @DeleteMapping(Routes.FAVORITES_ID)
    public ResponseEntity<Void> deleteFavorite(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            favoritesService.removeFavorite(request, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AuthenticationException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHENTICATION_EXCEPTION, exp);
        }catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }
}
