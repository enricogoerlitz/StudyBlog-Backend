package com.htwberlin.studyblog;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.models.BlogPostModel;
import com.htwberlin.studyblog.api.service.BlogPostService;
import com.htwberlin.studyblog.api.utilities.ENV;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
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
	CommandLineRunner run(ApplicationUserService userService, BlogPostService blogPostService) {
		log.warn(Boolean.toString(blogPostService == null));
		return args -> {
			getInitUsers().stream().forEach(user -> {
				userService.registerUser(user);
				getInitUserBlogPosts(user, 4).stream().forEach(post -> blogPostService.addBlogpost(post));
			});
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private List<ApplicationUserModel> getInitUsers() {
		var admin = new ApplicationUserModel(1l,"admin", ENV.getAdminPassword(), Role.ADMIN.name(), new ArrayList<>());
		var root = new ApplicationUserModel(2l,"root", ENV.getRootPassword(), Role.ADMIN.name(), new ArrayList<>());
		var testStudent = new ApplicationUserModel(3l,"teststudent", ENV.getStudentPassword(), Role.STUDENT.name(), new ArrayList<>());

		return Arrays.asList(admin, root, testStudent);
	}

	private List<BlogPostModel> getInitUserBlogPosts(ApplicationUserModel user, int postCounts) {
		var posts = new ArrayList<BlogPostModel>();
		for(int i = 0; i < postCounts; i++) {
			posts.add(new BlogPostModel(
				null,
				"Blogpost" + i + " - " + user.getUsername(),
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et.",
				new Date(),
				null,
				user
			));
		}

		return posts;
	}
}
