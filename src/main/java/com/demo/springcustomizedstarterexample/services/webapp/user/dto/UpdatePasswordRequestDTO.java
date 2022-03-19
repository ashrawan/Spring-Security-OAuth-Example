package com.demo.springcustomizedstarterexample.services.webapp.user.dto;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {

    private Long userId;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
