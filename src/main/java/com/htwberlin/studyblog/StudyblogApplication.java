package com.htwberlin.studyblog;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.modelsEntity.BlogPostEntity;
import com.htwberlin.studyblog.api.modelsEntity.FavoritesEntity;
import com.htwberlin.studyblog.api.service.BlogPostService;
import com.htwberlin.studyblog.api.service.FavoritesService;
import com.htwberlin.studyblog.api.utilities.ENV;
import com.htwberlin.studyblog.api.service.ApplicationUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootApplication
public class StudyblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyblogApplication.class, args);
	}

	@Bean
	CommandLineRunner run(ApplicationUserService userService, BlogPostService blogPostService, FavoritesService favouriteService) {
		log.warn(Boolean.toString(blogPostService == null));
		return args -> {
			getInitUsers().stream().forEach(user -> {
				try {
					userService.registerUser(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
				getInitUserBlogPosts(user, 4).stream().forEach(post -> {
					var addedPost = blogPostService.addBlogpostDEV(post);
					getInitFavourites(user, addedPost).forEach(fav -> {
						var f = favouriteService.addFavoriteDEV(user, addedPost);
						log.warn("is favourite added: " + Boolean.toString(f != null));
					});
				});
			});
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private List<ApplicationUserEntity> getInitUsers() {
		var admin = new ApplicationUserEntity(1l,"admin", ENV.getAdminPassword(), Role.ADMIN.name());
		var root = new ApplicationUserEntity(2l,"root", ENV.getRootPassword(), Role.ADMIN.name());
		var testStudent = new ApplicationUserEntity(3l,"teststudent", ENV.getStudentPassword(), Role.STUDENT.name());

		return Arrays.asList(admin, root, testStudent);
	}

	private List<BlogPostEntity> getInitUserBlogPosts(ApplicationUserEntity user, int postCounts) {
		var posts = new ArrayList<BlogPostEntity>();
		for(int i = 0; i < postCounts; i++) {
			posts.add(new BlogPostEntity(
				null,
				"Blogpost" + i + " - " + user.getUsername(),
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et.",
				new Date(),
				null,
				user,
				new ArrayList<>()
			));
		}

		return posts;
	}

	private List<FavoritesEntity> getInitFavourites(ApplicationUserEntity user, BlogPostEntity blogPost) {
		var fav1 = new FavoritesEntity(null, user, blogPost);
		return Arrays.asList(fav1);
	}
}
