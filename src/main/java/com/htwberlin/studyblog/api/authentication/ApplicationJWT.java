package com.htwberlin.studyblog.api.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.htwberlin.studyblog.api.models.ApplicationUserModel;
import com.htwberlin.studyblog.api.modelsEntity.ApplicationUserEntity;
import com.htwberlin.studyblog.api.utilities.ENV;
import com.htwberlin.studyblog.api.helper.HttpResponseWriter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/** ApplicationJWT
 *  Static Class for managing Application-JWT-Token
 */
public final class ApplicationJWT {
    public static final String JWT_KEY_STUDYBLOG = "studyblog_jwt";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_KEY = "roles";

    // last number correspond to the minutes
    private static final int expiredDuration = 1000 * 60 * 120;

    /**
     * Creates a JWT-Token. This Token contains the username and the roles the authenticated user.
     * @param request http.request
     * @param authResult Authentication from AuthenticationManager Class
     * @return String JWT-Token
     */
    public static String createToken(HttpServletRequest request, Authentication authResult) {
        User user = (User)authResult.getPrincipal();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDuration))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(Algorithm.HMAC256(ENV.getJWTSecret().getBytes()));
    }

    /**
     * Creates a JWT-Token with the data of the user (for refreshing).
     * @param request http.request
     * @param user ApplicationUserEntity
     * @return String JWT-Token
     */
    public static String createRefreshedToken(HttpServletRequest request, ApplicationUserEntity user) {
        var currentToken = getTokenVerificationResponseFromRequest(request).validate();
        var authority = currentToken.getAuthenticationToken();

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredDuration))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", authority.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining()))
                .sign(Algorithm.HMAC256(ENV.getJWTSecret().getBytes()));
    }

    /**
     * Tries to verify a JWT-Token. Creates and return a JWTVerificationResponse.
     * @param jwt String JWT-Token
     * @return JWTVerificationResponse
     */
    public static JWTVerificationResponse validateToken(String jwt) {
        if (jwt == null || jwt.isEmpty())
            return new JWTVerificationResponse(false, "The JWT-Token was null!");

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

    /**
     * Tries to read the JWT-Token from the Authorization-Header and removes the "Bearer "-Prefix".
     * Returns Null or the JWT.
     * @param request http.request
     * @return String JWT-Token without "Bearer "-Prefix or Null
     */
    public static String getTokenFromRequestHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
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
     * Returns the CurrentValidUser (ApplicationUserModel) from the "studyblog_jwt"-Cookie.
     * @param request http.request
     * @return ApplicationUserModel CurrentValidUser or Null
     */
    public static ApplicationUserModel getUserFromRequestCookie(HttpServletRequest request) {
        return validateToken(getTokenFromRequestCookie(request)).getUser();
    }

    /**
     * Returns the CurrentValidUser (ApplicationUserModel) from the Authorization-Header
     * @param request http.request
     * @return ApplicationUserModel CurrentValidUser or Null
     */
    public static ApplicationUserModel getUserFromRequestHeader(HttpServletRequest request) {
        return validateToken(getTokenFromRequestHeader(request)).getUser();
    }

    /**
     * Refreshes the "studyblog_jwt"-Cookie and set it to a new created JWT-Token.
     * The JWT-Token contains the username, roles and an Authentication-Object.
     * Writes a JSON-Message with the setted JWT-Token to the http-response.
     * @param request http.request
     * @param response http.response
     * @param authResult Authentication (from AuthenticationManager)
     */
    public static void refreshJWTCookie(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        String jwtToken = createToken(request, authResult);
        addJWTCookie(request, response, jwtToken);
        HttpResponseWriter.writeJsonResponse(response, Map.of(ApplicationJWT.JWT_KEY_STUDYBLOG, jwtToken));
    }

    /**
     * Refreshes the "studyblog_jwt"-Cookie and set it to a new created JWT-Token.
     * The JWT-Token contains the username, roles and an Authentication-Object.
     * Writes a JSON-Message with the setted JWT-Token to the http-response.
     * @param request http.request
     * @param response http.response
     * @param user ApplicationUserEntity
     */
    public static void refreshJWTCookie(HttpServletRequest request, HttpServletResponse response, ApplicationUserEntity user) {
        String jwtToken = createRefreshedToken(request, user);
        addJWTCookie(request, response, jwtToken);
        HttpResponseWriter.writeJsonResponse(response, Map.of(ApplicationJWT.JWT_KEY_STUDYBLOG, jwtToken));
    }

    /**
     * Adds a JWT-Token to a new Cookie or refreshes the "studyblog_jwt"-Cookie with the new JWT-Token.
     * @param request http.request
     * @param response http.response
     * @param jwtToken String JWT-Token
     */
    private static void addJWTCookie(HttpServletRequest request, HttpServletResponse response, String jwtToken) {
        var cookies = request.getCookies();
        if(cookies == null) {
            response.addCookie(new Cookie(JWT_KEY_STUDYBLOG, jwtToken));
            return;
        }

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(JWT_KEY_STUDYBLOG)) {
                cookie.setValue(jwtToken);
                response.addCookie(cookie);
            }
        }
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
