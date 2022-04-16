package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.service.BlogPostService;
import com.htwberlin.studyblog.api.utilities.HttpResponseWriter;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping(Routes.BLOGPOSTS)
    public ResponseEntity<List<BlogPostEntity>> getBlogposts(HttpServletResponse response) throws IOException {
        try {
            var blogPosts = blogPostService.getBlogPosts();
            if(blogPosts == null) return ResponseEntity.notFound().build();

            return ResponseEntity.status(HttpStatus.OK).body(blogPosts);
        } catch(Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> addBlogPost(HttpServletResponse response, @RequestBody BlogPostModel blogPost) throws IOException {
        try {
            var addedBlogPost = blogPostService.addBlogpost(blogPost);
            if(addedBlogPost == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            return ResponseEntity.created(null).body(addedBlogPost);
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> updateBlogPost(HttpServletRequest request, HttpServletResponse response, @RequestBody BlogPostModel blogPost) throws IOException {
        try {
            var updatedBlogPost = blogPostService.updateBlogPost(request, blogPost);
            if(updatedBlogPost == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            return ResponseEntity.ok().body(updatedBlogPost);
        } catch(AuthorizationServiceException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch(Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(Routes.ADMIN_BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> updateBlogPostByAdmin(HttpServletResponse response, @RequestBody BlogPostModel blogPost) throws IOException {
        try {
            var updatedBlogPost = blogPostService.updateBlogPostByAdmin(blogPost);
            if (updatedBlogPost == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            return ResponseEntity.status(HttpStatus.OK).body(updatedBlogPost);
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(Routes.BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPost(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException {
        try {
            blogPostService.deleteBlogPost(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (AuthorizationServiceException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch(IllegalArgumentException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(Routes.ADMIN_BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPostByAdmin(HttpServletResponse response, @PathVariable String id) throws IOException {
        try {
            blogPostService.deleteBlogPostByAdmin(id);
            return ResponseEntity.ok().build();
        } catch(IllegalArgumentException exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception exp) {
            HttpResponseWriter.writeJsonResponse(response, HttpResponseWriter.error(exp));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
