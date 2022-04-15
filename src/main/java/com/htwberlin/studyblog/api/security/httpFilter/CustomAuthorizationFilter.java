package com.htwberlin.studyblog.api.security.httpFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // TODO: if condition outsourcen
        String authHeader = request.getHeader(AUTHORIZATION);
        if(request.getServletPath().equals("/api/v1/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(FORBIDDEN.value());
            // outsourcen (two times used)
            var errorMap = Map.of("access_denied", "No access. Please enter a valid Access-Token");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
        }
        // TODO: outsource (this validates the jwt token
        try {
            // TODO: bearer in constants
            String jwt = authHeader.substring("Bearer ".length());
            var algorithm = Algorithm.HMAC256("secret".getBytes());
            var jwtVerifyer = JWT.require(algorithm).build();
            var decodedJwt = jwtVerifyer.verify(jwt);
            String username = decodedJwt.getSubject();
            // TODO: clamname in JWT.Role
            String role = decodedJwt.getClaim("roles").asString(); // maybe string[]!
            var authToken = new UsernamePasswordAuthenticationToken(username, null, Arrays.asList(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("jwt token is valid!");
            filterChain.doFilter(request, response);
        } catch(Exception exp) {
            // when jwt is invalid
            log.warn("user trys to access without a valid jwt. exp: {}", exp.getMessage());
            response.setHeader("error", exp.getMessage());
            //response.sendError(FORBIDDEN.value());
            response.setStatus(FORBIDDEN.value());
            // outsourcen (two times used)
            var errorMap = Map.of("error", exp.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
        }
    }
}
