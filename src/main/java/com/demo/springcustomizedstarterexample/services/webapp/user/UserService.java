package com.demo.springcustomizedstarterexample.services.webapp.user;

import com.demo.springcustomizedstarterexample.services.common.GenericResponseDTO;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // CRUD
    List<UserDTO> getAllUsers(Pageable pageable);

    UserDTO findUserByEmail(String email);

    Optional<UserDTO> findOptionalUserByEmail(String email);

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    // Email Verification
    GenericResponseDTO<Boolean> sendVerificationEmail(String email);

    GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO);

    // Reset Password
    GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO);

    // Other extras
    GenericResponseDTO<Boolean> userEmailExists(String email);

    GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest);

}
