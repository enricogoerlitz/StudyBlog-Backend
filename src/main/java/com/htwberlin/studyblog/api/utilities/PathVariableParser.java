package com.htwberlin.studyblog.api.utilities;

public final class PathVariableParser {
    public static Long parseLong(String longValue) {
        Long validLongValue = Long.valueOf(longValue);
        if(validLongValue == null) throw new IllegalArgumentException("id must be a Long value");

        return validLongValue;
    }
}
