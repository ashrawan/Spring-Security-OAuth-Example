package com.demo.springcustomizedstarterexample.services.mail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageTemplateCodeUtil {

    public static final String REG_COMPANY_NAME = "XYZ COMPANY";


    public enum TemplatesPath {
        WELCOME_MAIL("/welcome.ftlh"),
        EMAIL_VERIFICATION_MAIL("/verification-code.ftlh"),
        RESET_PASSWORD_MAIL("/reset-password.ftlh");

        private String templatePath;

        TemplatesPath(String templatePath) {
            this.templatePath = templatePath;
        }

        public String getTemplatePath() {
            return templatePath;
        }
    }

    public static class TemplateKeys {
        // Default Registered template values constant Keys
        public static final String REGCompanyName = "REGCompanyName";
        public static final String REGCompanyStreet = "REGCompanyStreet";
        public static final String REGCompanyCountry = "REGCompanyCountry";
        public static final String REGCompanyPhone = "REGCompanyPhone";

        // Welcome user template Keys
        public static final String welcomedUserFirstName = "firstName";
        public static final String setupItemList = "setupItemList";
        public static final String visitOfficialSite = "visitOfficialSite";

        // Verification code template Keys
        public static final String verificationUserFirstName = "firstName";
        public static final String linkEmailVerification = "linkEmailVerification";

        // Password reset template keys
        public static final String linkPasswordReset = "linkPasswordReset";

    }


    // Template values
    public static final Map<String, String> templateDefaultValuesMap = Collections.unmodifiableMap(
            new HashMap<>() {{
                put(TemplateKeys.REGCompanyName, REG_COMPANY_NAME);
                put(TemplateKeys.REGCompanyStreet, "Fictional Street");
                put(TemplateKeys.REGCompanyCountry, "Country - Nepal");
                put(TemplateKeys.REGCompanyPhone, "+0 000 000 0000");
            }}
    );

    public static final String subjectWelcomeEmail = "Welcome to the Team";
    public static final List<String> welcomeTemplateSetupList = List.of(
            "Laptop and required resources",
            "Walk through, project setups, and assistance from the assigned team member",
            "Session with the manager and processes walk-through",
            "Introduction with the team");

    public static final String subjectVerifyEmail = "Verify email address";

    public static final String subjectResetPasswordEmail = "Password reset request";


}
