package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.service.BlogPostService;
import com.htwberlin.studyblog.api.utilities.ResponseEntityExceptionManager;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping(Routes.BLOGPOSTS)
    public ResponseEntity<List<BlogPostModel>> getBlogposts(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(blogPostService.getBlogPosts(request));
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostModel> addBlogPost(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var addedBlogPost = blogPostService.addBlogpost(request, blogPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedBlogPost);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostModel> updateBlogPost(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var updatedBlogPost = blogPostService.updateBlogPost(request, blogPost);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedBlogPost);
        } catch(AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.ADMIN_BLOGPOSTS)
    public ResponseEntity<BlogPostModel> updateBlogPostByAdmin(HttpServletResponse response, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var updatedBlogPost = blogPostService.updateBlogPostByAdmin(blogPost);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedBlogPost);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPost(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        try {
            blogPostService.deleteBlogPost(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(response, AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.ADMIN_BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPostByAdmin(HttpServletResponse response, @PathVariable String id) {
        try {
            blogPostService.deleteBlogPostByAdmin(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch(IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(response, ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(response, EXCEPTION, exp);
        }
    }
}
