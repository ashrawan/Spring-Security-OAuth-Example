## Spring Security with OAuth2, and Custom JWT
- OAuth2 with (Google, Facebook, Github), (additional example.yml included)
- Additional Custom JWT (register and Login)
- Email based - register/login, freemarker-templates, registration-verification and password-reset

 Link:
- [x] Viewing Backend: https://github.com/ashrawan/Spring-Security-OAuth-Example
- [ ] Frontend: https://github.com/ashrawan/Angular-Security-OAuthJWT-Example

## Flow Overview:

[ __OAUTH2 CONFIGURATION PROPERTIES__ @see: resources -> _application.yml_ ]

```yaml

# By default, popular provider details isn't required @See org.springframework.security.config.oauth2.client.CommonOAuth2Provider
# To achieve same through java configuration, @See org.springframework.security.oauth2.client.registration.ClientRegistrationRepository that register ClientRegistration Object

  security:
    oauth2:
      client:
        registration:
          google:
            clientId: ${GOOGLE_CLIENT_ID}
            clientSecret: ${GOOGLE_CLIENT_SECRET}
            redirectUri: "{baseUrl}/oauth2/callback/{registrationId}"
            scope: email, profile

          facebook:
            clientId: ${FACEBOOK_CLIENT_ID}
            clientSecret: ${FACEBOOK_CLIENT_SECRET}
            redirectUri: "{baseUrl}/oauth2/callback/{registrationId}"
            scope: email, public_profile

```

[ __MAIN WEB SECURITY CONFIGURATION__ @see: config -> _WebSecurityConfig.java_ ]

```java
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // withDefaults() uses a Bean by the name of CorsConfigurationSource
                .cors(withDefaults())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
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
}
```

---

## #. Run using maven wrapper/executable or via your preferred IDE

- __Note__:
  - To run: Change datasource properties @See application.yml -> ```spring: datasource: ``` (create database "test")
  - To view flow: Change logging level as needed @See application.yml -> ```logging:```

```cmd
    ./mvnw spring-boot:run     // (using maven-wrapper)
                    
                     OR     
                    
    mvn spring-boot:run        // ( using env mvn executable)
```
---  

### 1. CUSTOM REGISTRATION AND OAUTH2 REGISTRATION

- __CUSTOM__: Registration is done via custom PUBLIC endpoint -> "/auth/register" { email/username, password, address etc. }
- __OAUTH2__: Registration requires setting up `_CustomOAuth2UserService.java_ which extends DefaultOAuth2UserService`   
  - we carry authentication with OAuth2 providers. After authentication, we will receive email or some form of identification,  
  - And then we check whether the specified email belonging to that provider already exists in our system/database or not  
  - If its new user, we registered that user directly,  
  - OPTIONAL: to promote users for new Sign up confirmation: we can structure flow e.g. setting up flag in db "userApprovedRegistration: false" or
  emailVerified, with verification code - if user already exists, flow continue to LOG IN / SIGN IN OAUTH2 process (see below)

### 2. CUSTOM / OAUTH2 - LOGIN OR SIGN IN

- __CUSTOM__: JWT token are created via separate custom PUBLIC endpoint -> "/auth/login"  { email/username, password }
- __OAUTH2__: In case of OAuth2, authentication via OAuth2 providers is done with _CustomOAuth2UserService.java_
    - on SUCCESSFUL authentication custom class `_OAuth2AuthenticationSuccessHandler.java_ extends SimpleUrlAuthenticationSuccessHandler` triggers
    - This class handles custom jwt token creation and respond back to redirect_uri

```
String redirect_uri="http://my-ui-app.com/login"
String targetUrl = UriComponentsBuilder.fromUriString(redirect_uri)
                .queryParam("token", createdCustomJwtToken)
                .build().toUriString();

// e.g. targetUrl - http://my-ui-app.com/login?token=created-custom-jwt-token
redirectStrategy.sendRedirect(request, response, targetUrl);
```

```text
# 1. Requesting OAuth2 Sign Up / Sign In
e.g. http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8080/oauth2/redirect  
http://localhost:8080/oauth2/authorize/google?redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fredirect

# 2. After clicking on consent screen
For all OAuth2 providers, On Successful OAuth2User Authentication Request process OAuth2UserRequest (CustomOAuth2UserService) 
  - Determine is this [ New Sign Up ] or [ Existing Sign In ]
  -  Create Principle Object that will set Authentication object
  - Continue flow to either OAuth2AuthenticationSuccessHandler OR OAuth2AuthenticationFailureHandler

# 3. Flow, After Success/Failure Authentication  
- OAuth2AuthenticationSuccessHandler.java
    - We create ( Custom JWT Token OR use OAuthProvider JWT token ) and respond back to redirect_uri , e.g. http://my-ui-app.com/oauth2/redirectPage?token=jwtToken )
    - http://localhost:8080/oauth2/redirect?token=jwtToken )

- OAuth2AuthenticationFailureHandler.java
    - We send authentication error response to the redirect_uri , e.g. http://my-ui-app.com/oauth2/redirectPage?error=Sorry-Couldnt-retrieve-your-email-from-Provider )
    - http://localhost:8080/oauth2/redirect?error=authenticationException-OR-localizedMessage-OR-custom-message )
```

### 3. ACCESSING RESOURCES ( ACCESS CONTROL MECHANISM )

- If user requests __PUBLIC__ endpoints
    - PUBLIC endpoints are freely available and skips Security filter chain

- If user requests into __SECURED__ endpoints e.g. "/api/userProfile"
    - Requesting WITHOUT token i.e. "Authorization" header
        - SecurityContext is empty, ` (path request matcher or anyRequest() ) -> .authenticated()` will catch, OR `globalMethodSecurity` will catch
        - And forward it to _CustomAuthenticationEntryPoint.java_ for __SC_UNAUTHORIZED__ response

    - Requesting WITH token i.e. "Authorization" header has jwt token
        - _JWTAuthenticationFilter.java_ Validates Token and set SecurityContext -> Authentication Object
        - Since, Authentication Object is available, spring security filter continues the flow
        - Further permission checks are done in spring security Expression-Based Access Control or other Access Control mechanism

`jwtAuthenticationFilter`: A custom filter to validate and parse jwtToken and Set Authentication Object

- Checks "Authorization" header is present
    - Not Present: Continues the filterChain
    - If present: Validates Token and set SecurityContext -> Authentication Object

## Structure

- By Default, config classes uses Field Injection `@Autowired`, other class uses constructor injection
- Properties defined in `application.yml` are initialized into `AppProperties.java`, default values are initialized in same class
- For Multiple properties usage from `AppProperties appProperties`, values are initialized in `@PostConstruct` to avoid code clutter
- Uses Lombok `@Getter @Setter` for getter/setter generation
- Exception thrown for authentication ( like BadCredentialsException) and other custom defined exceptions are handled from `@ControllerAdvice`
