package com.htwberlin.studyblog.api.helper;

/** PathVariableParser
 *  Static Class for converting PathVariables from the http-request.
 */
public final class PathVariableParser {
    /**
     * Converts a String PathVariable to a Long-Value or throws an exception.
     * @param longValue Long
     * @return Long
     */
    public static Long parseLong(String longValue) {
        return Long.valueOf(longValue);
    }
}
