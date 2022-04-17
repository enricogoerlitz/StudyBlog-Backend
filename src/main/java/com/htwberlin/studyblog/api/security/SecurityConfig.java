package com.htwberlin.studyblog.api.security;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.security.httpFilter.CustomAuthenticationFilter;
import com.htwberlin.studyblog.api.security.httpFilter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers(POST, "/api/v1/login/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/api/v1/users/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/v1/auth/**").permitAll();
        http.authorizeRequests().antMatchers(POST, "/api/v1/auth/**").permitAll();
        http.authorizeRequests().antMatchers(PUT, "/api/v1/users/**").hasAnyAuthority(Role.ADMIN.name(), Role.STUDENT.name());
        http.authorizeRequests().antMatchers(GET, "/api/v1/users/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(GET, "/api/v1/admin/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().anyRequest().authenticated();

        // TODO: outsource
        var customAuthFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthFilter.setFilterProcessesUrl("/api/v1/login");
        http.addFilter(customAuthFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // TODO: Delete?
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
