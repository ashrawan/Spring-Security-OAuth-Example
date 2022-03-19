package com.demo.springcustomizedstarterexample.security.oauth;

import com.demo.springcustomizedstarterexample.security.oauth.common.HttpCookieOAuth2AuthorizationRequestRepository;
import com.demo.springcustomizedstarterexample.utils.AppWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.demo.springcustomizedstarterexample.security.oauth.common.OAuth2Util.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * 1. Flow comes here "onAuthenticationFailure()", If OAuth2 Authentication Fails from - CustomOAuth2UserService
 * - We send authentication error response to the redirect_uri ( e.g. http://my-ui-app.com/oauth2/redirectPage?error=authenticationException-localizedMessage-or-custom-message )
 * - Since its failure response, (we aren't sending any tokens or data ) so, we don't need to validate the redirect_uri for security measures
 *
 * 2. By default, OAuth2 uses Session based AuthorizationRequestRepository, since we are using Cookie based AuthorizationRequestRepository
 * - We clear authorizationRequest stored in our cookie, before sending redirect response
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = AppWebUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
