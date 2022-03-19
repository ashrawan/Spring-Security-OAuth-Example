package com.demo.springcustomizedstarterexample.config;

import com.demo.springcustomizedstarterexample.security.CustomAuthenticationEntryPoint;
import com.demo.springcustomizedstarterexample.security.CustomUserDetailsService;
import com.demo.springcustomizedstarterexample.security.JWTAuthenticationFilter;
import com.demo.springcustomizedstarterexample.security.oauth.CustomOAuth2UserService;
import com.demo.springcustomizedstarterexample.security.oauth.OAuth2AuthenticationFailureHandler;
import com.demo.springcustomizedstarterexample.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.demo.springcustomizedstarterexample.security.oauth.common.HttpCookieOAuth2AuthorizationRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // CustomUserDetailsService - To process custom user SignUp/SignIn request
    // CustomOAuth2UserService - To process OAuth user SignUp/SignIn request
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PasswordEncoder passwordEncoder;

    // CustomAuthenticationEntryPoint - Unauthorized Access handler
    // JWTAuthenticationFilter - Retrieves request JWT token and, validate and set Authentication
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    // Cookie based repository, OAuth2 Success and Failure Handler
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService,
                             CustomOAuth2UserService customOAuth2UserService,
                             PasswordEncoder passwordEncoder,
                             CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                             JWTAuthenticationFilter jwtAuthenticationFilter,
                             HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                             OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                             OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.passwordEncoder = passwordEncoder;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // withDefaults() uses a Bean by the name of CorsConfigurationSource
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                //  CookieCsrfTokenRepository config - all put,post,delete request (form curl or browser or any) requires csrf token
                // .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeRequests(a -> a
                        .antMatchers("/auth/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(o -> o
                        .authorizationEndpoint().baseUri("/oauth2/authorize")
                        .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        .and()
                        .redirectionEndpoint().baseUri("/oauth2/callback/*")
                        .and()
                        .userInfoEndpoint().userService(customOAuth2UserService)
                        .and()
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler));

        // Add our custom Token based authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

}
