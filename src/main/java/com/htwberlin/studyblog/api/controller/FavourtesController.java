package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.modelsEntity.FavouriteEntity;
import com.htwberlin.studyblog.api.service.FavouriteService;
import com.htwberlin.studyblog.api.utilities.HttpResponseWriter;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.API)
@Slf4j
public class FavourtesController {
    private final FavouriteService favouriteService;
    // by user
    @GetMapping("/v1/favourites/{id}")
    public ResponseEntity<List<FavouriteEntity>> getFavourites(@PathVariable String id, HttpServletResponse response) throws IOException {
        log.warn("id: " + id);
        Long parsedId = Long.valueOf(id);
        if(parsedId == null) {
            HttpResponseWriter.writeJsonResponse(response, Map.of("error", "Could not parse param id to Long"));
            ResponseEntity.badRequest().build();
        }
        log.warn("id: " + parsedId);
        var favourites = favouriteService.getFavouritesByCreator(parsedId);
        if(favourites == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(favourites);
    }
}
