package com.demo.springcustomizedstarterexample.security.oauth;

import com.demo.springcustomizedstarterexample.config.AppProperties;
import com.demo.springcustomizedstarterexample.security.JWTTokenProvider;
import com.demo.springcustomizedstarterexample.security.oauth.common.HttpCookieOAuth2AuthorizationRequestRepository;
import com.demo.springcustomizedstarterexample.utils.AppWebUtils;
import com.demo.springcustomizedstarterexample.utils.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static com.demo.springcustomizedstarterexample.security.oauth.common.OAuth2Util.ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME;
import static com.demo.springcustomizedstarterexample.security.oauth.common.OAuth2Util.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * 1. Flow comes here "onAuthenticationSuccess()", After successful OAuth2 Authentication (see: CustomOAuth2UserService )
 * - We create Custom JWT Token and respond back to redirect_uri ( e.g. http://my-ui-app.com/oauth2/redirectPage?token=generatedJwtToken )
 * - We validate the redirect_uri for security measures, to send token to only our authorized redirect origins
 * <p>
 * 2. By default, OAuth2 uses Session based AuthorizationRequestRepository, since we are using Cookie based AuthorizationRequestRepository
 * - We clear authorizationRequest stored in our cookie, before sending redirect response
 */
@Service
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = AppWebUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        Optional<String> originalRequestUri = AppWebUtils.getCookie(request, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isRedirectOriginAuthorized(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String token = jwtTokenProvider.createJWTToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .queryParam(ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME, originalRequestUri)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
                                                 HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isRedirectOriginAuthorized(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return Arrays.stream(appProperties.getOAuth2().getAuthorizedRedirectOrigins())
                .anyMatch(authorizedRedirectOrigin -> {
                    URI authorizedURI = URI.create(authorizedRedirectOrigin);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
