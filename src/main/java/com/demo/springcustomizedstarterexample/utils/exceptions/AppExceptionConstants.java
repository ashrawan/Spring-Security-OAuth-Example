package com.demo.springcustomizedstarterexample.utils.exceptions;

public final class AppExceptionConstants {

    // Auth Exception
    public static final String BAD_LOGIN_CREDENTIALS = "Bad Credentials - Invalid username or password";
    public static final String ACCOUNT_NOT_ACTIVATED = "Account not activated - Please verify your email or reprocess verification using forgot-password.";

    public static final String UNAUTHORIZED_ACCESS = "Insufficient authorization access";

    // User Exception
    public static final String USER_RECORD_NOT_FOUND = "User doesn't exists";
    public static final String USER_EMAIL_NOT_AVAILABLE = "This email isn't available";
    public static final String OLD_PASSWORD_DOESNT_MATCH = "Old and New Password doesn't match";
    public static final String MATCHING_VERIFICATION_RECORD_NOT_FOUND = "Provided verification request doesn't seems correct";
    public static final String INVALID_PASSWORD_RESET_REQUEST = "Provided Password reset request doesn't seems correct";


    // Raw-Data Exception
    public static final String REQUESTED_RESOURCE_NOT_FOUND = "Couldn't find the requested resource";

}
