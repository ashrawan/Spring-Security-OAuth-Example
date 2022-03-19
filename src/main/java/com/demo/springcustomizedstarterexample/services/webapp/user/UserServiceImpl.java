package com.demo.springcustomizedstarterexample.services.webapp.user;

import com.demo.springcustomizedstarterexample.config.AppProperties;
import com.demo.springcustomizedstarterexample.entities.UserEntity;
import com.demo.springcustomizedstarterexample.repository.UserRepository;
import com.demo.springcustomizedstarterexample.security.AppSecurityUtils;
import com.demo.springcustomizedstarterexample.security.oauth.common.SecurityEnums;
import com.demo.springcustomizedstarterexample.services.common.GenericResponseDTO;
import com.demo.springcustomizedstarterexample.services.mail.EmailService;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.*;
import com.demo.springcustomizedstarterexample.utils.AppUtils;
import com.demo.springcustomizedstarterexample.utils.exceptions.AppExceptionConstants;
import com.demo.springcustomizedstarterexample.utils.exceptions.BadRequestException;
import com.demo.springcustomizedstarterexample.utils.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final AppProperties appProperties;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           EmailService emailService,
                           AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.appProperties = appProperties;
    }


    @Override
    public List<UserDTO> getAllUsers(Pageable pageable) {
        Page<UserEntity> pageUserEntities = userRepository.findAll(pageable);
        return userMapper.toDtoList(pageUserEntities.getContent());
    }

    @Override
    public UserDTO findUserByEmail(String userEmail) {
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userMapper.toDto(userEntity);
    }

    @Override
    public Optional<UserDTO> findOptionalUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userEntity -> userMapper.toDto(userEntity));
    }

    @Override
    public UserDTO getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        return userMapper.toDto(userEntity);
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDTO createUser(UserDTO requestUserDTO) {
        if (ObjectUtils.isEmpty(requestUserDTO.getRoles())) {
            requestUserDTO.setRoles(Set.of(AppSecurityUtils.ROLE_DEFAULT));
        }
        boolean isFromCustomBasicAuth = requestUserDTO.getRegisteredProviderName().equals(requestUserDTO.getRegisteredProviderName());
        if (isFromCustomBasicAuth && requestUserDTO.getPassword() != null) {
            requestUserDTO.setPassword(passwordEncoder.encode(requestUserDTO.getPassword()));
        }
        UserEntity userEntity = userMapper.toEntity(requestUserDTO);
        boolean existsByEmail = userRepository.existsByEmail(userEntity.getEmail());
        if (existsByEmail) {
            throw new ResourceNotFoundException(AppExceptionConstants.USER_EMAIL_NOT_AVAILABLE);
        }
        userRepository.save(userEntity);
        sendVerificationEmail(userEntity.getEmail());
        return userMapper.toDto(userEntity);
    }

    @Override
    public UserDTO updateUser(UserDTO reqUserDTO) {
        UserEntity userEntity = userRepository.findById(reqUserDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        userEntity.setFullName(reqUserDTO.getFullName());
        userEntity.setImageUrl(reqUserDTO.getImageUrl());
        userEntity.setPhoneNumber(reqUserDTO.getPhoneNumber());
        userRepository.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GenericResponseDTO<Boolean> sendVerificationEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        String verificationCode = AppUtils.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = appProperties.getMail().getVerificationCodeExpirationSeconds();
        userEntity.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        userEntity.setVerificationCode(verificationCode);
        MultiValueMap<String, String> appendQueryParamsToVerificationLink = constructEmailVerificationLinkQueryParams(
                userEntity.getEmail(), verificationCode, userEntity.getRegisteredProviderName());
        String fullName = userEntity.getFullName();
        String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
        userRepository.save(userEntity);
        emailService.sendVerificationEmail(userEntity.getEmail(), firstName, appendQueryParamsToVerificationLink);
        GenericResponseDTO<Boolean> genericResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return genericResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> sendResetPasswordEmail(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        UserEntity userEntity = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_EMAIL_NOT_AVAILABLE));
        String forgotPasswordVerCode = AppUtils.generateRandomAlphaNumericString(20);
        long verificationCodeExpirationSeconds = appProperties.getMail().getVerificationCodeExpirationSeconds();
        userEntity.setVerificationCodeExpiresAt(Instant.now().plusSeconds(verificationCodeExpirationSeconds));
        userEntity.setVerificationCode(forgotPasswordVerCode);
        MultiValueMap<String, String> appendQueryParamsToPasswordResetLink = constructPasswordResetLinkQueryParams(
                userEntity.getEmail(), forgotPasswordVerCode);
        String fullName = userEntity.getFullName();
        String firstName = fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
        userRepository.save(userEntity);
        emailService.sendPasswordResetEmail(userEntity.getEmail(), firstName, appendQueryParamsToPasswordResetLink);
        GenericResponseDTO<Boolean> genericResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return genericResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> verifyEmailAddress(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        Optional<UserEntity> optionalUserEntity = userRepository.verifyAndRetrieveEmailVerificationRequestUser(
                verifyEmailRequestDTO.getEmail(), verifyEmailRequestDTO.getAuthProviderId(), verifyEmailRequestDTO.getVerificationCode());
        UserEntity userEntity = optionalUserEntity
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.MATCHING_VERIFICATION_RECORD_NOT_FOUND));
        userEntity.setEmailVerified(Boolean.TRUE);
        userEntity.setVerificationCodeExpiresAt(null);
        userEntity.setVerificationCode(null);
        userRepository.save(userEntity);
        emailService.sendWelcomeEmail(userEntity.getEmail(), userEntity.getFullName());
        GenericResponseDTO<Boolean> emailVerifiedResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return emailVerifiedResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> verifyAndProcessPasswordResetRequest(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Optional<UserEntity> optionalUserEntity = userRepository.verifyAndRetrieveForgotPasswordRequestUser(
                resetPasswordRequestDTO.getEmail(), SecurityEnums.AuthProviderId.app_custom_authentication, resetPasswordRequestDTO.getForgotPasswordVerCode());
        UserEntity userEntity = optionalUserEntity
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.INVALID_PASSWORD_RESET_REQUEST));
        userEntity.setVerificationCodeExpiresAt(null);
        userEntity.setVerificationCode(null);
        userEntity.setEmailVerified(true);
        userEntity.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        userRepository.save(userEntity);
        GenericResponseDTO<Boolean> emailVerifiedResponseDTO = GenericResponseDTO.<Boolean>builder().response(true).build();
        return emailVerifiedResponseDTO;
    }

    @Override
    public GenericResponseDTO<Boolean> userEmailExists(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);
        return GenericResponseDTO.<Boolean>builder().response(existsByEmail).build();
    }

    @Override
    public GenericResponseDTO<Boolean> updatePassword(UpdatePasswordRequestDTO updatePasswordRequest) {
        UserEntity userEntity = userRepository.findById(updatePasswordRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AppExceptionConstants.USER_RECORD_NOT_FOUND));
        boolean passwordMatches = passwordEncoder.matches(updatePasswordRequest.getOldPassword(), userEntity.getPassword());
        if (!passwordMatches) {
            throw new BadRequestException(AppExceptionConstants.OLD_PASSWORD_DOESNT_MATCH);
        }
        userEntity.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        userRepository.save(userEntity);
        return GenericResponseDTO.<Boolean>builder().response(true).build();
    }

    private static MultiValueMap<String, String> constructEmailVerificationLinkQueryParams(String email,
                                                                                           String verificationCode,
                                                                                           SecurityEnums.AuthProviderId authProvider) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the verification link, must sync with VerifyEmailRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("registeredProviderName", authProvider.toString());
        appendQueryParams.add("verificationCode", verificationCode);
        return appendQueryParams;
    }

    private static MultiValueMap<String, String> constructPasswordResetLinkQueryParams(String email,
                                                                                       String forgotPasswordVerCode) {
        MultiValueMap<String, String> appendQueryParams = new LinkedMultiValueMap<>();
        // Generated QueryParams for the password reset link, must sync with ResetPasswordRequestDTO
        appendQueryParams.add("email", email);
        appendQueryParams.add("forgotPasswordVerCode", forgotPasswordVerCode);
        return appendQueryParams;
    }

}
