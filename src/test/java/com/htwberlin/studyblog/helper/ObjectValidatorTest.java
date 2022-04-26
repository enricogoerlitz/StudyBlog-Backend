package com.htwberlin.studyblog.helper;

import com.htwberlin.studyblog.api.helper.ObjectValidator;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.AuthenticationException;

import static com.htwberlin.studyblog.api.utilities.ResponseEntityException.*;

public class ObjectValidatorTest implements WithAssertions {

    private final ObjectValidator objValidator = new ObjectValidator();

    @Test
    @DisplayName("Should throw an exception, by passing an null-argument")
    void throw_illegal_exception_if_null() {
        // given
        // pass

        // when
        // pass

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> objValidator.validateNotNullObject(null));
    }

    @Test
    @DisplayName("Should throw an custom exception with custom errormessage, by passing an null-argument")
    void throw_custom_exception_or_return_same_obj() throws Exception {
        // given
        // pass

        // when
        // pass

        // then
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, USERNAME_NOT_FOUND_EXCEPTION, "USERNAME_NOT_FOUND_EXCEPTION"));

        assertThatExceptionOfType(AuthenticationException.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, AUTHENTICATION_EXCEPTION, "AUTHENTICATION_EXCEPTION"));

        assertThatExceptionOfType(AuthorizationServiceException.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, AUTHORIZATION_SERVICE_EXCEPTION, "AUTHORIZATION_SERVICE_EXCEPTION"));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, ILLEGAL_ARGUMENT_EXCEPTION, "ILLEGAL_ARGUMENT_EXCEPTION"));

        assertThatExceptionOfType(DuplicateKeyException.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, DUPLICATE_KEY_EXCEPTION, "DUPLICATE_KEY_EXCEPTION"));

        assertThatExceptionOfType(Exception.class)
                .isThrownBy(() -> objValidator.getValidObjOrThrowException(null, EXCEPTION, "EXCEPTION"));

        assertThat(((String)objValidator.getValidObjOrThrowException("ThisNotNullObj", EXCEPTION, "EXCEPTION")).equals("ThisNotNullObj"));
    }

}
