package com.demo.springcustomizedstarterexample.controller;

import com.demo.springcustomizedstarterexample.services.auth.AuthenticationService;
import com.demo.springcustomizedstarterexample.services.auth.dtos.AuthResponseDTO;
import com.demo.springcustomizedstarterexample.services.auth.dtos.LoginRequestDTO;
import com.demo.springcustomizedstarterexample.services.auth.dtos.RegisterUserRequestDTO;
import com.demo.springcustomizedstarterexample.services.common.GenericResponseDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.UserService;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.ForgotPasswordRequestDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.ResetPasswordRequestDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UserDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.VerifyEmailRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService,
                                    UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Authentication API: loginUser: ", loginRequest.getEmail());
        AuthResponseDTO authResponseDTO = authenticationService.loginUser(loginRequest);
        return new ResponseEntity<>(authResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
        log.info("Authentication API: registerUser: ", registerUserRequestDTO.getEmail());
        UserDTO userDTO = authenticationService.registerUser(registerUserRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/resend-verification-email")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam("email") String email) {
        log.info("Authentication API: resendVerificationEmail: ", email);
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendVerificationEmail(email);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/check-verification-code")
    public ResponseEntity<?> checkVerificationCode(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        log.info("Authentication API: checkVerificationCode: ", verifyEmailRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyEmailAddress(verifyEmailRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }

    @PostMapping("/send-forgot-password")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        log.info("Authentication API: sendResetPasswordEmail: ", forgotPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> resendVerificationEmailStatus = userService.sendResetPasswordEmail(forgotPasswordRequestDTO);
        return new ResponseEntity<>(resendVerificationEmailStatus, HttpStatus.OK);
    }

    @PostMapping("/process-password-reset")
    public ResponseEntity<?> verifyAndProcessPasswordResetRequest(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Authentication API: verifyAndProcessPasswordResetRequest: ", resetPasswordRequestDTO.getEmail());
        GenericResponseDTO<Boolean> checkVerificationCodeStatus = userService.verifyAndProcessPasswordResetRequest(resetPasswordRequestDTO);
        return new ResponseEntity<>(checkVerificationCodeStatus, HttpStatus.OK);
    }
}
