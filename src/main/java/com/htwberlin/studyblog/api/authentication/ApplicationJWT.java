package com.htwberlin.studyblog.api.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.htwberlin.studyblog.api.helper.PathVariableParser;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.utilities.ENV;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/** ApplicationJWT
 *  Static Class for managing Application-JWT-Token
 */
@Slf4j
public final class ApplicationJWT {
    public static final String JWT_KEY_STUDYBLOG = "studyblog_jwt";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_KEY = "roles";

    // last number correspond to the minutes
    private static final int expiredDuration = 1000 * 60 * 60 * 24 * 60;

    public static String createUserModelToken(HttpServletRequest request, ApplicationUserEntity user) {
        String userObjectString = user.getId() + ";" + user.getUsername();
        return JWT.create()
                .withSubject(userObjectString)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDuration))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getRole())
                .sign(Algorithm.HMAC256(ENV.getJWTSecret().getBytes()));
    }

    public static JWTVerificationResponse validateToken(String jwt) {
        if (jwt == null || jwt.isEmpty())
            return new JWTVerificationResponse(false, "The JWT-Token was null!");

        try {
            var algorithm = Algorithm.HMAC256(ENV.getJWTSecret().getBytes());
            var jwtVerifier = JWT.require(algorithm).build();
            var decodedJwt = jwtVerifier.verify(jwt);

            String[] userIdAndName = decodedJwt.getSubject().split(";");
            Long userId = PathVariableParser.parseLong(userIdAndName[0]);
            String username = userIdAndName[1];
            String role = decodedJwt.getClaim(ROLE_KEY).asString();

            return new JWTVerificationResponse(true, new ApplicationUserModel(userId, username, role));
        } catch (Exception exp) {
            return new JWTVerificationResponse(false, exp.getMessage());
        }
    }

    /**
     * Tries to read the JWT-Token from the Authorization-Header and removes the "Bearer "-Prefix".
     * Returns Null or the JWT.
     * @param request http.request
     * @return String JWT-Token without "Bearer "-Prefix or Null
     */
    public static String getTokenFromRequestHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        log.warn("AuthHeader: " + authHeader);
        return authHeader == null ? null : removeBearerPrefix(authHeader);
    }

    /**
     * Tries to read the JWT-Token from the "studyblog_jwt"-Cookie.
     * Returns Null or the JWT.
     * @param request http.request
     * @return String JWT-Token or Null
     */
    public static String getTokenFromRequestCookie(HttpServletRequest request) {
        var jwtCookies = getJWTCookies(request);
        if(jwtCookies == null || jwtCookies.size() == 0)
            return null;

        return jwtCookies.get(0).getValue();
    }
    /**
     * Returns a JWTVerificationResponse from
     *      (1.) the "studyblog_jwt"-Cookie or if this is invalid from the
     *      (2.) the Authorization-Header and removes the "Bearer "-Prefix".
     * @param request http.request
     * @return JWTVerificationResponse
     */
    public static JWTVerificationResponse getTokenVerificationResponseFromRequest(HttpServletRequest request) {
        var validatedToken = validateToken(getTokenFromRequestCookie(request));
        if(validatedToken.isValid()) return validatedToken;

        return validateToken(getTokenFromRequestHeader(request));
    }

    /**
     * Returns the CurrentValidUser (ApplicationUserModel) from
     *      (1.) the "studyblog_jwt"-Cookie or if this null from the
     *      (2.) the Authorization-Header or Null.
     * @param request http.request
     * @return ApplicationUserModel CurrentValidUser or Null
     */
    public static ApplicationUserModel getUserFromJWT(HttpServletRequest request) {
        var cookieUser = getUserFromRequestCookie(request);
        if(cookieUser != null)
            return cookieUser;

        return getUserFromRequestHeader(request);
    }
    /**
     * Returns the CurrentValidUser (JWTUser) from the "studyblog_jwt"-Cookie.
     * @param request http.request
     * @return JWTUser CurrentValidUser or Null
     */
    public static ApplicationUserModel getUserFromRequestCookie(HttpServletRequest request) {
        return validateToken(getTokenFromRequestCookie(request)).getUser();
    }

    /**
     * Returns the CurrentValidUser (JWTUser) from the Authorization-Header
     * @param request http.request
     * @return JWTUser CurrentValidUser or Null
     */
    public static ApplicationUserModel getUserFromRequestHeader(HttpServletRequest request) {
        return validateToken(getTokenFromRequestHeader(request)).getUser();
    }


    /**
     * Return a list of cookies, containing all Cookies with the name of "studyblog_jwt"
     * or returns null, if there are no Cookies with this name.
     * @param request http.request
     * @return List of StudyBlog-Cookies or Null
     */
    private static List<Cookie> getJWTCookies(HttpServletRequest request) {
        var cookies = request.getCookies();

        if(cookies == null) return null;
        List<Cookie> jwtCookies = stream(request.getCookies())
                .filter(p -> p.getName().equals(ApplicationJWT.JWT_KEY_STUDYBLOG)).toList();

        if(jwtCookies.size() == 0) return null;

        return jwtCookies;
    }

    /**
     * Removes the "Bearer "-Prefix
     * @param jwtWithPrefix String JWT-Token with Bearer-Prefix (from Authorization-Header)
     * @return String cleaned JWT-Token
     */
    private static String removeBearerPrefix(String jwtWithPrefix) {
        if(!jwtWithPrefix.startsWith(ApplicationJWT.BEARER_PREFIX))
            return jwtWithPrefix;

        return jwtWithPrefix.substring(ApplicationJWT.BEARER_PREFIX.length());
    }
}
