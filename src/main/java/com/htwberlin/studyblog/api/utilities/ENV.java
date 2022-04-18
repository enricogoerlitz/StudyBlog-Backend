package com.htwberlin.studyblog.api.utilities;

/** ENV
 *  Static Class for getting dummy passwords
 */
public final class ENV {
    public static String getJWTSecret() {
        return System.getenv().get("JWT_SECRET");
    }

    public static String getRootPassword() {
        return System.getenv().get("ROOT_PW");
    }

    public static String getAdminPassword() {
        return System.getenv().get("ADMIN_PW");
    }

    public static String getStudentPassword() {
        return System.getenv().get("STUDENT_PW");
    }
}
