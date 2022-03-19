package com.demo.springcustomizedstarterexample.services.webapp.user.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {

    private String email;

    private String forgotPasswordVerCode;

    private String newPassword;
}
