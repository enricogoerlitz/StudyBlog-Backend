package com.htwberlin.studyblog.api.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/** HttpResponseWriter
 *  Static Class for converting a Map-Object to a JSON-Object and write that JSON-Object to the Http-Response
 */
public final class HttpResponseWriter {
    /**
     * Converts a Map-Object to a JSON-Object and writes that JSON-Object to the Http-Response.
     * @param response http.request
     * @param writingMap Map<String, String> Message as Key-Value-Pair
     */
    public static void writeJsonResponse(HttpServletResponse response, Map<String, String> writingMap) {
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), writingMap);
        } catch (IOException ignored) {}
    }

    /**
     * Converts an Exception to an errormessage and returns that es Map-Object
     * @param exp Exception
     * @return Map<String, String> as errormessage
     */
    public static Map<String, String> error(Exception exp) {
        return Map.of("error_msg", exp.getMessage());
    }
}
