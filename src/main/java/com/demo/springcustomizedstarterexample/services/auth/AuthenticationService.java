package com.demo.springcustomizedstarterexample.services.auth;

import com.demo.springcustomizedstarterexample.services.auth.dtos.AuthResponseDTO;
import com.demo.springcustomizedstarterexample.services.auth.dtos.LoginRequestDTO;
import com.demo.springcustomizedstarterexample.services.auth.dtos.RegisterUserRequestDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UserDTO;

public interface AuthenticationService {

    AuthResponseDTO loginUser(LoginRequestDTO loginRequest);

    UserDTO registerUser(RegisterUserRequestDTO registerUserRequestDTO);

}
