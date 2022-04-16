package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.FavoritesModel;
import com.htwberlin.studyblog.api.service.FavoritesService;
import com.htwberlin.studyblog.api.utilities.HttpResponseWriter;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.API)
@Slf4j
public class FavoritesController {
    private final FavoritesService favoritesService;

    @GetMapping("/v1/favorites")
    public ResponseEntity<List<FavoritesModel>> getFavoritesByUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            var favorites = favoritesService.getFavoritesByCreator(request);
            if(favorites == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            return ResponseEntity.status(HttpStatus.OK).body(favorites);
        } catch (AuthenticationException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/v1/favorites/{id}")
    public ResponseEntity<FavoritesModel> addFavoriteToUser(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException {
        try {
            var addedFavorite = favoritesService.addFavourite(request, id);
            // TODO: remove all == null => exception handling!
            if(addedFavorite == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            return ResponseEntity.status(HttpStatus.CREATED).body(addedFavorite);
        } catch (AuthenticationException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DuplicateKeyException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/v1/favorites/{id}")
    public ResponseEntity<Void> deleteFavorite(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException {
        try {
            favoritesService.removeFavorite(request, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AuthenticationException exp) {
            // TODO: komplett in util outsourcen (return authenticationException)
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch(Exception exp) {
            // TODO: komplett in util outsourcen (return serverException)
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
