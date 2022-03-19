package com.demo.springcustomizedstarterexample.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AppSecurityUtils {

    public static final String ROLE_DEFAULT = "ROLE_DEFAULT";

    /**
     * Converts list of roles into Collection of GrantedAuthority
     *
     * @param roles
     * @return Collection<? extends GrantedAuthority>
     */
    public static Collection<? extends GrantedAuthority> convertRolesSetToGrantedAuthorityList(Set<String> roles) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
            authorities.add(grantedAuthority);
        }
        return authorities;
    }

    /**
     * Converts Collection of GrantedAuthority into list of roles
     *
     * @param grantedAuthorities
     * @return Set<String>
     */
    public static Set<String> convertGrantedAuthorityListToRolesSet(Collection<? extends GrantedAuthority> grantedAuthorities) {
        Set<String> roles = AuthorityUtils.authorityListToSet(grantedAuthorities);
        return roles;
    }

    /**
     * Get Authentication object from SecurityContextHolder
     *
     * @return Authentication object
     */
    public static Authentication getAuthenticationObject() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Get current user principle
     *
     * @return CustomUserDetails - principle object
     */
    public static CustomUserDetails getCurrentUserPrinciple() {
        Authentication authentication = getAuthenticationObject();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal);
            }
        }
        return null;
    }

    /**
     * Get current user id
     *
     * @return Long - user id
     */
    public static Optional<Long> getCurrentUserId() {
        Optional<Long> optionalUserId = Optional.ofNullable(getCurrentUserPrinciple())
                .map(customUserDetails -> customUserDetails.getUserEntity())
                .map(userEntity -> userEntity.getId());
        return optionalUserId;
    }


    /**
     * Check if user is Authenticated
     *
     * @return true - if user is Authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthenticationObject();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(AppSecurityUtils.ROLE_DEFAULT));
        }
        return false;
    }

}
