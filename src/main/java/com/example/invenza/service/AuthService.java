package com.example.invenza.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.invenza.config.security.JwtService;
import com.example.invenza.config.security.MemberUserDetails;
import com.example.invenza.dto.LoginRequest;
import com.example.invenza.dto.LoginResponse;
import com.example.invenza.util.Constants;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse authenticate(LoginRequest request) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(Constants.LOGIN_FAILED_MESSAGE);
        }

        MemberUserDetails userDetails = (MemberUserDetails) auth.getPrincipal();
        String jwtToken = jwtService.createLoginAccessToken(userDetails);
        return LoginResponse.of(jwtToken, userDetails);
    }

    public LoginResponse getCurrentUser() throws BadCredentialsException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new BadCredentialsException(Constants.INVALID_TOKEN_MESSAGE);
        }

        String jwt = (String) auth.getCredentials();
        var user = (MemberUserDetails) auth.getPrincipal();
        return LoginResponse.of(jwt, user);
    }

    public void forgotPassword(String email) {
        // TODO: Waiting for frontend Document to implement
    }
}
