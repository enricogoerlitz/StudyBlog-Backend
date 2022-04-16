package com.htwberlin.studyblog.api.controller;

import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.models.BlogPostRequestModel;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.service.BlogPostService;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** TODO: save all manipulation routes by authorize by user => is user also creator
 *  with JWT-Cookie and with AuthHeader JWT
 */

/** TODO: wrap everything in a try-catch-block
 *
 */

/** TODO: add the following routes
 *   - get by owner
 *   - put edit by admin
 *   - delete delete by admin
 */
@RestController
@RequestMapping(Routes.API)
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping(Routes.BLOGPOSTS)
    public ResponseEntity<List<BlogPostEntity>> getBlogposts() {
        var blogPosts = blogPostService.getBlogPosts();
        if(blogPosts == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(blogPosts);
    }

    @PostMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> addBlogPost(@RequestBody BlogPostRequestModel blogPost) {
        var addedBlogPost = blogPostService.addBlogpost(blogPost);
        if(addedBlogPost == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.created(null).body(addedBlogPost);
    }

    // TODO: save by creator.username == jwt.username => forbidden
    @PutMapping(Routes.BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> updateBlogPost(@RequestBody BlogPostRequestModel blogPost) {
        var updatedBlogPost = blogPostService.updateBlogPost(blogPost);
        if(updatedBlogPost == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().body(updatedBlogPost);
    }

    @PutMapping(Routes.ADMIN_BLOGPOSTS)
    public ResponseEntity<BlogPostEntity> updateBlogPostByAdmin(@RequestBody BlogPostRequestModel blogPost) {
        var updatedBlogPost = blogPostService.updateBlogPost(blogPost);
        if(updatedBlogPost == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().body(updatedBlogPost);
    }

    // TODO: save by creator.username == jwt.username => forbidden
    @DeleteMapping(Routes.BLOGPOSTS)
    public ResponseEntity<Void> deleteBlogPost(Long id) {
        try {
            blogPostService.deleteBlogPost(id);
            return ResponseEntity.ok().build();
        } catch (Exception exp) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(Routes.ADMIN_BLOGPOSTS)
    public ResponseEntity<Void> deleteBlogPostByAdmin(Long id) {
        try {
            blogPostService.deleteBlogPost(id);
            return ResponseEntity.ok().build();
        } catch (Exception exp) {
            return ResponseEntity.badRequest().build();
        }
    }
}
