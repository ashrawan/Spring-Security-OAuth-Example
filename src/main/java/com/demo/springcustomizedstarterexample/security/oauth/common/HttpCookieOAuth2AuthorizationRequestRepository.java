package com.demo.springcustomizedstarterexample.security.oauth.common;

import com.demo.springcustomizedstarterexample.utils.AppUtils;
import com.demo.springcustomizedstarterexample.utils.AppWebUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.demo.springcustomizedstarterexample.security.oauth.common.OAuth2Util.*;

/**
 * Cookie based repository for storing Authorization requests
 * <p>
 * By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
 * the authorization request. But, since our service is stateless, we can't save it in the session.
 * We'll use cookie instead.
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    /**
     * Load authorization request from cookie
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

        Assert.notNull(request, "request cannot be null");

        return AppWebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> deserializeCookie(cookie))
                .orElse(null);
    }

    /**
     * Save authorization request in cookie
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");

        if (authorizationRequest == null) {

            AppWebUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            AppWebUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            AppWebUtils.deleteCookie(request, response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME);
            return;
        }

        // Setting up authorizationRequest COOKIE, redirectUri COOKIE and originalRequestUri COOKIE
        String redirectUri = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        String originalRequestUri = request.getParameter(ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME);
        AppWebUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, AppUtils.serialize(authorizationRequest));
        AppWebUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUri);
        AppWebUtils.addCookie(response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME, originalRequestUri);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {

        OAuth2AuthorizationRequest originalRequest = loadAuthorizationRequest(request);
        AppWebUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return originalRequest;
    }

    @Deprecated
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        throw new UnsupportedOperationException("Spring Security shouldn't have called the deprecated removeAuthorizationRequest(request)");
    }


    private OAuth2AuthorizationRequest deserializeCookie(Cookie cookie) {
        return AppUtils.deserialize(cookie.getValue());
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        AppWebUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        AppWebUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        AppWebUtils.deleteCookie(request, response, ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME);
    }
}
