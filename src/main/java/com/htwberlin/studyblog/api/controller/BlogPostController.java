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
import javax.validation.Valid;
import java.util.List;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

/** BlogPostController
 *  RESTController for BlogPost-Routes
 */
@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
@Slf4j
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping(Routes.BLOGPOSTS)
    public ResponseEntity<List<BlogPostModel>> getBlogposts(HttpServletRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(blogPostService.getBlogPosts(request));
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PostMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostModel> addBlogPost(HttpServletRequest request, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var addedBlogPost = blogPostService.addBlogpost(request, blogPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedBlogPost);
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostModel> updateBlogPost(HttpServletRequest request, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var updatedBlogPost = blogPostService.updateBlogPost(request, blogPost);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedBlogPost);
        } catch(AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @PutMapping(Routes.ADMIN_BLOGPOSTS)
    public ResponseEntity<BlogPostModel> updateBlogPostByAdmin(HttpServletRequest request, @Valid @RequestBody BlogPostModel blogPost) {
        try {
            var updatedBlogPost = blogPostService.updateBlogPostByAdmin(request, blogPost);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedBlogPost);
        } catch(AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPost(HttpServletRequest request, @PathVariable String id) {
        try {
            blogPostService.deleteBlogPost(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (AuthorizationServiceException exp) {
            return ResponseEntityExceptionManager.handleException(AUTHORIZATION_SERVICE_EXCEPTION, exp);
        } catch(IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }

    @DeleteMapping(Routes.ADMIN_BLOGPOSTS + "/{id}")
    public ResponseEntity<Void> deleteBlogPostByAdmin(HttpServletRequest request, @PathVariable String id) {
        try {
            blogPostService.deleteBlogPostByAdmin(request, id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch(IllegalArgumentException exp) {
            return ResponseEntityExceptionManager.handleException(ILLEGAL_ARGUMENT_EXCEPTION, exp);
        } catch (Exception exp) {
            return ResponseEntityExceptionManager.handleException(EXCEPTION, exp);
        }
    }
}
