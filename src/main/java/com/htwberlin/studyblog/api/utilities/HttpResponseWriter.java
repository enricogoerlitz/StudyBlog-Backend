package com.htwberlin.studyblog.api.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public final class HttpResponseWriter {
    public static void writeJsonResponse(HttpServletResponse response, Map<String, String> writingMap) {
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), writingMap);
        } catch (IOException e) {}
    }

    public static Map<String, String> error(Exception exp) {
        return Map.of("error_msg", exp.getMessage());
    }
}
