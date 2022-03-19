package com.demo.springcustomizedstarterexample.security.oauth.common;

import java.util.Map;

public abstract class CustomAbstractOAuth2UserInfo {
    protected Map<String, Object> attributes;

    public CustomAbstractOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
