package com.demo.springcustomizedstarterexample.security.oauth.common;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.util.Map;

public class OAuth2Util {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    // UI-App/Web-Client will use this param to redirect flow to appropriate page
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public static final String ORIGINAL_REQUEST_URI_PARAM_COOKIE_NAME = "original_request_uri";

    /**
     * Populate CustomAbstractOAuth2UserInfo for specific OAuthProvider
     */
    public static CustomAbstractOAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                                 Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(SecurityEnums.AuthProviderId.google.toString())) {
            return new GoogleCustomAbstractOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SecurityEnums.AuthProviderId.facebook.toString())) {
            return new FacebookCustomAbstractOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(SecurityEnums.AuthProviderId.github.toString())) {
            return new GithubCustomAbstractOAuth2UserInfo(attributes);
        } else {
            throw new InternalAuthenticationServiceException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
    

}
