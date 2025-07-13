# Spring Security OAuth Example

This repository demonstrates how to combine OAuth2 logins with custom JWT support in a Spring Boot application. It includes sample configurations for Google, Facebook and GitHub authentication providers and a classic username/password flow with email verification.

## Project Layout

```
src/
  main/
    java/com/demo/springcustomizedstarterexample
      config/        -> Spring configuration (security, beans, MVC)
      controller/    -> REST endpoints
      entities/      -> JPA entities
      repository/    -> Spring Data repositories
      security/      -> JWT and OAuth2 integration
      services/      -> Business and auth services
      utils/         -> Helpers and exceptions
    resources/       -> application.yml, mail templates
  test/               -> Basic context test
```

The main entry point is `SpringCustomizedStarterExampleApplication`. Security is configured in `WebSecurityConfig` which sets up stateless JWT authentication, OAuth2 login flow and registers a custom filter that parses JWT tokens.

## Key Features

- OAuth2 login with Google, Facebook and GitHub
- Custom JWT tokens issued on login or OAuth2 success
- Email based registration, verification and password reset using Freemarker templates
- Repository and service layers built with Spring Data JPA

### Configuration Highlights

See `application.yml` for database, mail and OAuth client settings. Beans such as `PasswordEncoder` and `CorsConfigurationSource` are declared in the configuration classes under `config`.

### Security Layer

`JWTAuthenticationFilter` extracts tokens from the `Authorization` header. Tokens are created and validated by `JWTTokenProvider`. OAuth2 requests are handled by `CustomOAuth2UserService` along with success and failure handlers.

### Email Templates

Welcome, verification and password reset emails use Freemarker templates found in `src/main/resources/mail-templates`.

## Getting Started

Ensure you have a database (MySQL by default) and update OAuth credentials in `application.yml`. Run the application with:

```bash
./mvnw spring-boot:run
```

## Learn More

To dive deeper, explore:

1. **Spring Security OAuth2** – how OAuth2 login integrates with JWT generation.
2. **JWT best practices** – see how custom claims are added in `JWTTokenProvider`.
3. **Freemarker templating** – customize the templates used by `EmailService`.
4. **Spring Data JPA auditing** – entities extend an auditable base for automatic created/modified timestamps.

The project follows standard Spring Boot layering with controllers → services → repositories, so new contributors can start by experimenting with the authentication endpoints and browsing the configuration classes.


