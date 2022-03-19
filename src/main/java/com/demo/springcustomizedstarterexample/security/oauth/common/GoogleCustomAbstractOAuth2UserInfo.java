package com.demo.springcustomizedstarterexample.security.oauth.common;

import java.util.Map;

public class GoogleCustomAbstractOAuth2UserInfo extends CustomAbstractOAuth2UserInfo {

    public GoogleCustomAbstractOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
