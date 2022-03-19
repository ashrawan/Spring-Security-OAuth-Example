package com.demo.springcustomizedstarterexample.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "myapp")
@Slf4j
@Getter
@Setter
public class AppProperties {

    public AppProperties() {
        log.info("Application Properties Initialized");
    }

    private String appName = "My Stater App";

    private String officialCompanyName = "";

    private String officialCompanyDomain = "";

    // Mail config
    private Mail mail = new Mail();

    // CORS configuration
    private Cors cors = new Cors();

    // JWT token generation related properties
    private Jwt jwt = new Jwt();

    // Custom specific OAuth2 Properties
    private OAuth2 oAuth2 = new OAuth2();

    // Custom Defaults App/Web/Rest/Misc Properties
    private Defaults defaults = new Defaults();

    @Getter
    @Setter
    public static class Mail {
        private String defaultEmailAddress;
        private long verificationCodeExpirationSeconds = 600; // 10 minutes
    }

    @Getter
    @Setter
    public static class Cors {

        private String[] allowedOrigins;
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
        private String[] allowedHeaders = {"*"};
        private String[] exposedHeaders = {"*"};
        private long maxAge = 3600L;
    }

    @Getter
    @Setter
    public static class Jwt {

        private String secretKey;
        private boolean isSecretKeyBase64Encoded = false;
        private long expirationMillis = 864000000L; // 10 days
        // For short-lived tokens and cookies
        private int shortLivedMillis = 120000; // Two minutes
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private String[] authorizedRedirectOrigins;
        private int cookieExpireSeconds = 120; // Two minutes
    }

    @Getter
    @Setter
    public static class Defaults {
        private int defaultPageStart = 0;
        private int defaultPageSize = 50;
    }

}
