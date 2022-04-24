package com.htwberlin.studyblog.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/** SecurityConfig
 *  Class for Security configurations
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
/*
        // define authorizations of explicit routes
        http.authorizeRequests().antMatchers(POST, Routes.API + Routes.LOGIN + "/**").permitAll();
        http.authorizeRequests().antMatchers(GET, Routes.API + Routes.LOGIN + "/**").permitAll();
        http.authorizeRequests().antMatchers(POST, Routes.API + Routes.LOGIN + "/**").permitAll();
        http.authorizeRequests().antMatchers(POST, Routes.API + Routes.USERS + "/**").permitAll();
        http.authorizeRequests().antMatchers(PUT, Routes.API + Routes.USERS + "/**").hasAnyAuthority(Role.ADMIN.name(), Role.STUDENT.name());
        http.authorizeRequests().antMatchers(GET, Routes.API + Routes.USERS + "/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(GET, Routes.API + Routes.ADMIN + "/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(POST, Routes.API + Routes.ADMIN + "/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(PUT, Routes.API + Routes.ADMIN + "/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().antMatchers(DELETE, Routes.API + Routes.ADMIN + "/**").hasAnyAuthority(Role.ADMIN.name());
        http.authorizeRequests().anyRequest().authenticated();

        // set custom auth-filter
        var customAuthFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthFilter.setFilterProcessesUrl(Routes.API + Routes.LOGIN);
        http.addFilter(customAuthFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

 */
    }
}
