package com.demo.springcustomizedstarterexample;

import com.demo.springcustomizedstarterexample.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareUserImpl")
@EnableConfigurationProperties(AppProperties.class)
public class SpringCustomizedStarterExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCustomizedStarterExampleApplication.class, args);
    }

}
