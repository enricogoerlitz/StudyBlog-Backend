package com.htwberlin.studyblog.api.security;

import com.htwberlin.studyblog.api.authentication.Role;
import com.htwberlin.studyblog.api.security.httpFilter.CustomAuthenticationFilter;
import com.htwberlin.studyblog.api.security.httpFilter.CustomAuthorizationFilter;
import com.htwberlin.studyblog.api.utilities.Routes;
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

/** SecurityConfig
 *  Class for Security configurations
 */
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
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
