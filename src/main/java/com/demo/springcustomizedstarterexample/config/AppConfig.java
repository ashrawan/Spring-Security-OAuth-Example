package com.demo.springcustomizedstarterexample.config;

import com.demo.springcustomizedstarterexample.utils.AppUtils;
import com.demo.springcustomizedstarterexample.utils.AppWebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class AppConfig {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public AppConfig(AppProperties appProperties,
                     ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public AppUtils appUtils() {
        return new AppUtils(objectMapper);
    }

    @Bean
    public AppWebUtils webUtils() {
        int cookieExpireSeconds = appProperties.getOAuth2().getCookieExpireSeconds();
        return new AppWebUtils(cookieExpireSeconds);
    }

}
