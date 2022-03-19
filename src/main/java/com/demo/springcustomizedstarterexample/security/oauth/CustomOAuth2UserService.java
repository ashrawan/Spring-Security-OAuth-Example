package com.demo.springcustomizedstarterexample.security.oauth;

import com.demo.springcustomizedstarterexample.entities.UserEntity;
import com.demo.springcustomizedstarterexample.security.AppSecurityUtils;
import com.demo.springcustomizedstarterexample.security.CustomUserDetails;
import com.demo.springcustomizedstarterexample.security.oauth.common.CustomAbstractOAuth2UserInfo;
import com.demo.springcustomizedstarterexample.security.oauth.common.OAuth2Util;
import com.demo.springcustomizedstarterexample.security.oauth.common.SecurityEnums;
import com.demo.springcustomizedstarterexample.services.webapp.user.UserMapper;
import com.demo.springcustomizedstarterexample.services.webapp.user.UserService;
import com.demo.springcustomizedstarterexample.services.webapp.user.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 1. After Users Agrees by clicking on consent screen (To allow our app to access users allowed resources)
 * - loadUser will trigger for all OAuth2 provider - (GitHub, Google, Facebook, Custom Auth Provider etc.)
 * <p>
 * 2. Retrieve attributes, from security.oauth2.core.user.OAuth2User which consists of { name, email, imageUrl and other attributes }
 * - Each registrationId will have their own attributes key (eg. google: picture, github: avatar_url etc),
 * - And Map this attributes specific to OAuth2 provider with-respect-to abstract CustomAbstractOAuth2UserInfo
 * <p>
 * 3. Determine is this [ New Sign Up ] or [ Existing Sign In ]
 * - Sign In (email will be present in our database)  OR
 * - Sign Up ( if don't have user email, we need to register user, and save email into db)
 * <p>
 * 4. Create Principle Object i.e. CustomUserDetails implements OAuth2User
 * - return security.oauth2.core.user.OAuth2User that will set Authentication object, ( similar to CustomUserDetailsService - method loadUserByUsername )
 * <p>
 * 5. On completion "processOAuth2User()" Flow Jumps to either OAuth2AuthenticationSuccessHandler or OAuth2AuthenticationFailureHandler
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest,
                                         OAuth2User oAuth2User) {
        // Mapped OAuth2User to specific CustomAbstractOAuth2UserInfo for that registration id
        // clientRegistrationId - (google, facebook, gitHub, or Custom Auth Provider - ( keyClock, okta, authServer etc.)
        String clientRegistrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo = OAuth2Util.getOAuth2UserInfo(clientRegistrationId, oAuth2User.getAttributes());

        // Check if the email is provided by the OAuthProvider
        SecurityEnums.AuthProviderId registeredProviderId = SecurityEnums.AuthProviderId.valueOf(clientRegistrationId);
        String userEmail = customAbstractOAuth2UserInfo.getEmail();
        if (!StringUtils.hasText(userEmail)) {
            throw new InternalAuthenticationServiceException("Sorry, Couldn't retrieve your email from Provider " + clientRegistrationId + ". Email not available or Private by default");
        }

        // Determine is this [ Login ] or [ New Sign up ]
        // Sign In (email will be present in our database)  OR Sign Up ( if don't have user email, we need to register user, and save email into db)
        Optional<UserDTO> optionalUserByEmail = userService.findOptionalUserByEmail(userEmail);
        if (optionalUserByEmail.isEmpty()) {
            optionalUserByEmail = Optional.of(registerNewOAuthUser(oAuth2UserRequest, customAbstractOAuth2UserInfo));
        }
        UserDTO userDTO = optionalUserByEmail.get();
        if (userDTO.getRegisteredProviderName().equals(registeredProviderId)) {
            updateExistingOAuthUser(userDTO, customAbstractOAuth2UserInfo);
        } else {
            String incorrectProviderChoice = "Sorry, this email is linked with \"" + userDTO.getRegisteredProviderName() + "\" account. " +
                    "Please use your \"" + userDTO.getRegisteredProviderName() + "\" account to login.";
            throw new InternalAuthenticationServiceException(incorrectProviderChoice);
        }


        List<GrantedAuthority> grantedAuthorities = oAuth2User.getAuthorities().stream().collect(Collectors.toList());
        grantedAuthorities.add(new SimpleGrantedAuthority(AppSecurityUtils.ROLE_DEFAULT));
        UserEntity userEntity = userMapper.toEntity(userDTO);
        return CustomUserDetails.buildWithAuthAttributesAndAuthorities(userEntity, grantedAuthorities, oAuth2User.getAttributes());
    }

    private UserDTO registerNewOAuthUser(OAuth2UserRequest oAuth2UserRequest,
                                         CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo) {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(customAbstractOAuth2UserInfo.getName());
        userDTO.setEmail(customAbstractOAuth2UserInfo.getEmail());
        userDTO.setRegisteredProviderName(SecurityEnums.AuthProviderId.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        userDTO.setRegisteredProviderId(customAbstractOAuth2UserInfo.getId());
        userDTO.setRoles(Set.of(AppSecurityUtils.ROLE_DEFAULT));
        userDTO.setEmailVerified(true);
        UserDTO returnedUserDTO = userService.createUser(userDTO);
        return returnedUserDTO;
    }

    private void updateExistingOAuthUser(UserDTO existingUserDTO,
                                         CustomAbstractOAuth2UserInfo customAbstractOAuth2UserInfo) {
        existingUserDTO.setFullName(customAbstractOAuth2UserInfo.getName());
        existingUserDTO.setImageUrl(customAbstractOAuth2UserInfo.getImageUrl());
        UserDTO updatedUserDTO = userService.updateUser(existingUserDTO);
        BeanUtils.copyProperties(updatedUserDTO, existingUserDTO);
    }

}
