package com.htwberlin.studyblog;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.config.ENV;
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
import java.util.List;

@Slf4j
@SpringBootApplication
public class StudyblogApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyblogApplication.class, args);
	}

	@Bean
	CommandLineRunner run(ApplicationUserService userService) {
		log.warn("HERE IS THE URL: {}", System.getenv().get("DATASOURCE_URL"));
		return args -> {
			getInitUsers().stream().forEach(user -> userService.registerUser(user));
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private List<ApplicationUserModel> getInitUsers() {
		var admin = new ApplicationUserModel(1l,"admin", ENV.getAdminPassword(), Role.ADMIN.name());
		var root = new ApplicationUserModel(2l,"root",ENV.getRootPassword(), Role.ADMIN.name());
		var testStudent = new ApplicationUserModel(3l,"teststudent",ENV.getStudentPassword(), Role.STUDENT.name());
		var testVisitor = new ApplicationUserModel(4l,"testvisitor",ENV.getVisitorPassword(), Role.VISITOR.name());

		return Arrays.asList(admin, root, testStudent, testVisitor);
	}
}
