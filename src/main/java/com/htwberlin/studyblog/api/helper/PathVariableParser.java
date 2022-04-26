package com.htwberlin.studyblog.api.helper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** PathVariableParser
 *  Static Class for converting PathVariables from the http-request.
 */
@NoArgsConstructor
public class PathVariableParser {
    /**
     * Converts a String PathVariable to a Long-Value or throws an exception.
     * @param longValue Long
     * @return Long
     */
    public Long parseLong(String longValue) {
        return Long.valueOf(longValue);
    }
}
