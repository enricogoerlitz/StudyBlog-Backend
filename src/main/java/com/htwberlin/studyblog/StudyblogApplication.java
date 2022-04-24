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

@SpringBootApplication
@Slf4j
public class StudyblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyblogApplication.class, args);
	}

	/**
	 * Method initialisation
	 * @param userService _
	 * @param blogPostService _
	 * @param favouriteService _
	 * @return _
	 */
	@Bean
	CommandLineRunner run(ApplicationUserService userService, BlogPostService blogPostService, FavoritesService favouriteService) {
		return args -> {
			getInitUsers().forEach(user -> {
				try {
					userService.registerUser(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
				getInitUserBlogPosts(user, 3).forEach(post -> {
					var addedPost = blogPostService.addBlogPostDEV(post);
				});
				log.info("User " + user.getUsername() + " (" + user.getRole() + ") added to DB.");
			});
			log.info("✅ ✅ ✅  !>>>>>  🥳 APPLICATION READY 🥳  <<<<<!  ✅ ✅ ✅");
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Method for generating initialUsers
	 * @return List<ApplicationUserEntity>
	 */
	private List<ApplicationUserEntity> getInitUsers() {
		var admin = new ApplicationUserEntity(1L,"admin", ENV.getAdminPassword(), Role.ADMIN.name());
		var root = new ApplicationUserEntity(2L,"root", ENV.getRootPassword(), Role.ADMIN.name());
		var testStudent = new ApplicationUserEntity(3L,"teststudent", ENV.getStudentPassword(), Role.STUDENT.name());

		return Arrays.asList(admin, root, testStudent);
	}

	/**
	 * DEVELOPMENT
	 * @param user _
	 * @param postCounts _
	 * @return _
	 */
	private List<BlogPostEntity> getInitUserBlogPosts(ApplicationUserEntity user, int postCounts) {
		var posts = new ArrayList<BlogPostEntity>();
		for(int i = 0; i < postCounts; i++) {
			long id = (user.getId() - 1) * postCounts + i + 1;
			posts.add(new BlogPostEntity(
				id,
				"Blogpost" + id + " from user: " + user.getUsername(),
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam.",
				new Date(),
				null,
				user,
				new ArrayList<>()
			));
		}

		return posts;
	}
}
