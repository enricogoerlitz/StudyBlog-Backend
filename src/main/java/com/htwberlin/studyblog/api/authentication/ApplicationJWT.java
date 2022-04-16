package com.htwberlin.studyblog.api.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.utilities.ENV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public final class ApplicationJWT {
    public static final String JWT_KEY_STUDYBLOG = "studyblog_jwt";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_KEY = "roles";

    // last number correspond to the minutes
    private static final int expiredDuration = 1000 * 60 * 120;

    public static String createToken(HttpServletRequest request, Authentication authResult) {
        User user = (User)authResult.getPrincipal();
        return com.auth0.jwt.JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDuration))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(Algorithm.HMAC256(ENV.getJWTSecret().getBytes()));
    }

    public static JWTVerificationResponse validateToken(String jwt) {
        if (jwt == null || jwt.isEmpty()) return new JWTVerificationResponse(false, "The JWT was null");
        try {
            var algorithm = Algorithm.HMAC256(ENV.getJWTSecret().getBytes());
            var jwtVerifier = JWT.require(algorithm).build();
            var decodedJwt = jwtVerifier.verify(jwt);

            String username = decodedJwt.getSubject();
            String role = decodedJwt.getClaim(ROLE_KEY).asString();
            var authToken = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));

            return new JWTVerificationResponse(true, authToken);
        } catch (Exception exp) {
            return new JWTVerificationResponse(false, exp.getMessage());
        }
    }

    public static String getTokenFromRequestHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null) return null;
        log.info("validate by header");

        return removeBearerPrefix(authHeader);
    }

    public static String getTokenFromRequestCookie(HttpServletRequest request) {
        var cookies = request.getCookies();

        if(cookies == null) return null;
        List<Cookie> optionalJwt = stream(request.getCookies())
                .filter(p -> p.getName().equals(ApplicationJWT.JWT_KEY_STUDYBLOG)).toList();

        if(optionalJwt.size() != 1) return null;

        log.info("validate by cookie");
        return optionalJwt.get(0).getValue();
    }

    public static JWTVerificationResponse getTokenFromRequest(HttpServletRequest request) {
        var cookieValidatedToken = validateToken(getTokenFromRequestCookie(request));
        if(cookieValidatedToken.isValid()) return cookieValidatedToken;

        return validateToken(getTokenFromRequestHeader(request));
    }

    public static ApplicationUserModel getUserFromRequestCookie(HttpServletRequest request) {
        return validateToken(getTokenFromRequestCookie(request)).getUser();
    }

    public static ApplicationUserModel getUserFromRequestHeader(HttpServletRequest request) {
        return validateToken(getTokenFromRequestHeader(request)).getUser();
    }

    public static ApplicationUserModel getUserFromJWT(HttpServletRequest request) {
        var cookieUser = getUserFromRequestCookie(request);
        if(cookieUser != null) return cookieUser;

        return getUserFromRequestHeader(request);
    }

    private static String removeBearerPrefix(String jwtWithPrefix) {
        return jwtWithPrefix.substring(ApplicationJWT.BEARER_PREFIX.length());
    }
}
