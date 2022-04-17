package com.htwberlin.studyblog.api.utilities;

public final class Validator {
    public static void validateNotNullObject(Object obj) {
        if(obj == null)
            throw new IllegalArgumentException("Entity User was null!");
    }
}
