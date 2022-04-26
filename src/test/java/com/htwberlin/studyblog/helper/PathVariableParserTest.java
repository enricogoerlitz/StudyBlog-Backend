package com.htwberlin.studyblog.helper;

import com.htwberlin.studyblog.api.helper.PathVariableParser;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathVariableParserTest implements WithAssertions {

    private final PathVariableParser pathVarParser = new PathVariableParser();

    @Test
    @DisplayName("Should throw an error or returning valid Long-value.")
    void throw_error_or_return_valid_Long_value_from_string() {
        // given
        Long longValue = 2381939312L;
        String invalidLongValue = "09382LLs";

        // when
        String validLongValue = Long.toString(longValue);

        // then
        assertThat(pathVarParser.parseLong(validLongValue).equals(longValue));
        assertThatExceptionOfType(NumberFormatException.class).isThrownBy(() -> pathVarParser.parseLong(invalidLongValue));
    }

}
