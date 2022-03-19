package com.demo.springcustomizedstarterexample.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * ` http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); `
 * See: WebSecurityConfig for complete configuration
 *
 * This filter just checks if the token is present in request Header, "Authorization" key
 * - If present => Validates Token AND set Authentication Object into SecurityContextHolder
 * - Else       => Continue filter chain
 *
 */
@Slf4j
@Service
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = jwtTokenProvider.getBearerTokenFromRequestHeader(request);
            if (StringUtils.hasText(jwt) && this.jwtTokenProvider.validateJWTToken(jwt)) {
                Authentication authentication = this.jwtTokenProvider.getAuthenticationFromToken(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            log.info("Security exception Expired JWT token for user {} - {}", ex.getClaims().getSubject(), ex.getMessage());
            response.sendError(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value(), "Expired JWT token");
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            log.info("Security exception {} ", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }


}
