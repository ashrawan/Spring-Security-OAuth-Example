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
  - Configure datasource using environment variables `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.  Example:
    ```bash
    export DB_URL=jdbc:mysql://<rds-endpoint>:3306/test
    export DB_USERNAME=root
    export DB_PASSWORD=example
    ```
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
        - SecurityContext is empty, ` (path request matcher or anyRequest() ) -> .authenticated()` will catch, or will fail from `globalMethodSecurity` expression if defined
        - And forward it to _CustomAuthenticationEntryPoint.java_ for __SC_UNAUTHORIZED__ response

    - Requesting WITH token i.e. "Authorization" header has jwt token
        - _JWTAuthenticationFilter.java_ Validates Token and set SecurityContext -> Authentication Object
        - Since, Authentication Object is available, spring security filter continues the flow
        - Further permission checks are done in spring security Expression-Based Access Control or any configured Access Control mechanisms

`jwtAuthenticationFilter`: A custom filter to validate and parse jwtToken and Set Authentication Object

- Checks "Authorization" header is present
    - Not Present: Continues the filterChain
    - If present: Validates Token and set SecurityContext -> Authentication Object

## Structure

- Properties defined in `application.yml` are initialized into `AppProperties.java`, default values are initialized in same class
- For Multiple properties usage from `AppProperties appProperties`, values are initialized in `@PostConstruct` to avoid code clutter
- Uses Lombok `@Getter @Setter` for getter/setter generation
- By Default, config classes uses Field Injection `@Autowired`, other class uses constructor injection
 - Exception thrown are handled from `@ControllerAdvice`

## Deployment

- CloudFormation template for a minimal RDS MySQL instance is available under `deploy/cloudformation/rds.yml`.
- Kubernetes manifests for running the application are located in `deploy/kubernetes` and reference secrets containing the datasource credentials and AWS access keys.

### Preparing the environment

1. Copy `.env.example` to `.env` and adjust credentials. The same file is used for Kubernetes secret creation and docker-compose.
2. Edit `deploy/cloudformation/config.json` to adjust the action, protection and service settings as needed.
3. If using GitHub Actions, define the secrets listed in `.github/workflows/README.md` (AWS credentials, `ECR_REGISTRY`, `KUBECONFIG`, etc.). The workflow in `.github/workflows/build.yml` builds and pushes the container image automatically.
   To run locally, build the Docker image and push it to your ECR registry:

```bash
aws ecr get-login-password --region $AWS_REGION | \
  docker login --username AWS --password-stdin $ECR_REGISTRY
docker build -t $ECR_REGISTRY/spring-oauth-example:latest .
docker push $ECR_REGISTRY/spring-oauth-example:latest
```

### Local development

A docker-compose setup is available under `deploy/docker-compose` which starts MySQL, LocalStack and the application container. LocalStack emulates AWS services so the application can be tested without a real AWS account. Ensure you have the `.env` file in the project root then run:

```bash
cd deploy/docker-compose
docker compose up --build
```

The application will be available on `http://localhost:8080` with MySQL exposed on port `3306` and LocalStack on `4566`.

### Deploying to AWS

1. Provision infrastructure using the helper script which applies `deploy/cloudformation/infra.yml`. The `config.json` file controls whether stacks are created or removed and which services are enabled:

```bash
cd deploy/cloudformation
./deploy.sh yourdbpassword example.com.
```

You can also trigger the `Manage Infrastructure` workflow in GitHub Actions (`infra.yml`) which runs the same script in CI using the secrets you configured.

Note the database endpoint from the stack outputs and update `DB_URL` in your `.env` file.

2. Create the Kubernetes secret with the environment variables:

```bash
cd deploy/kubernetes
./create-secret.sh
```

3. Deploy the application:

```bash
cd deploy/kubernetes
./deploy.sh
```

Alternatively trigger the workflows defined in `.github/workflows`. Use `deploy.yml` for Kubernetes deployments and `infra.yml` for managing AWS resources. Redeploy by rebuilding the Docker image and running the workflows again as needed.
