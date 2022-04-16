package com.htwberlin.studyblog.api.security.httpFilter;

import com.htwberlin.studyblog.api.authentication.ApplicationJWT;
import com.htwberlin.studyblog.api.authentication.JWTVerificationResponse;
import com.htwberlin.studyblog.api.utilities.HttpResponseWriter;
import com.htwberlin.studyblog.api.utilities.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final List<String> excludesUrls = Arrays.asList(
        Routes.API + Routes.LOGIN,
        Routes.API + Routes.AUTH + Routes.HELLO_WORLD,
        "/api/v1/users",
        "/api/v1/auth/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String route = request.getServletPath();
        if(isUrlExcluded(route)) {
            filterChain.doFilter(request, response);
            return;
        }

        var tokenValidation = getTokenValidation(request); // ApplicationJWT.validateToken(ApplicationJWT.getTokenFromRequestHeader(request));
        if(!tokenValidation.isValid()) {
            log.warn("jwt token is invalid! Error: {}", tokenValidation.getErrorMessage());
            response.setStatus(FORBIDDEN.value());
            HttpResponseWriter.writeJsonResponse(response, Map.of("access_denied", "No access. Please enter a valid Access-Token", "error_msg", tokenValidation.getErrorMessage()));
            return;
        }

        log.info("jwt token is valid!");
        SecurityContextHolder.getContext().setAuthentication(tokenValidation.getAuthenticationToken());
        filterChain.doFilter(request, response);
    }

    private static JWTVerificationResponse getTokenValidation(HttpServletRequest request) {
        String cookieToken = ApplicationJWT.getTokenFromRequestCookie(request);
        if(cookieToken != null) return ApplicationJWT.validateToken(cookieToken);

        return ApplicationJWT.validateToken(ApplicationJWT.getTokenFromRequestHeader(request));
    }

    private boolean isUrlExcluded(String route) {
        for(String url : excludesUrls) {
            if(route.equals(url)) return true;
        }

        return false;
    }
}
