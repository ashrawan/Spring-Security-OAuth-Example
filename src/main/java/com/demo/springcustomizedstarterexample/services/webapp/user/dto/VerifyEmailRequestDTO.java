package com.demo.springcustomizedstarterexample.services.webapp.user.dto;

import com.demo.springcustomizedstarterexample.security.oauth.common.SecurityEnums;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyEmailRequestDTO {

    private String email;

    private String verificationCode;

    @JsonProperty("registeredProviderName")
    private SecurityEnums.AuthProviderId authProviderId;
}
