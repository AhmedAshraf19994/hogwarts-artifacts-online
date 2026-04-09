package com.ahmed.hogwarts_artifacts_online.secuirty;

import com.ahmed.hogwarts_artifacts_online.client.jwtTokenWhiteListService.JwtTokenWhiteListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RedisTokenValidationFilter extends OncePerRequestFilter {

    private final JwtTokenWhiteListService jwtTokenWhiteListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //extract the token value and user id and check redis cache if the token is valid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String accessToken = jwt.getTokenValue();
            Long userIdAsLong = jwt.getClaim("userId");
            int userId = userIdAsLong.intValue();
            //checking the token exists in redis cache
            if(!jwtTokenWhiteListService.isTokenValid(userId, accessToken)) {
                throw new BadCredentialsException("Token is revoked");
            }
        }

        filterChain.doFilter(request,response);

    }
}
