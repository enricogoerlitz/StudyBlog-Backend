package com.htwberlin.studyblog.api.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.htwberlin.studyblog.api.utilities.ENV;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public final class ApplicationJWT {
    public static final String KEY_STUDYBLOG = "studyblog_jwt";
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
                .withClaim("roles", user.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.joining()))
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
            var authToken = new UsernamePasswordAuthenticationToken(username, null, Arrays.asList(new SimpleGrantedAuthority(role)));

            return new JWTVerificationResponse(true, authToken);
        } catch (Exception exp) {
            return new JWTVerificationResponse(false, exp.getMessage());
        }
    }

    public static String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null) return null;

        return authHeader.substring(ApplicationJWT.BEARER_PREFIX.length());
    }
}
