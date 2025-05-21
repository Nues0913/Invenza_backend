package com.example.invenza.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.config.security.JwtService;
import com.example.invenza.config.security.MemberUserDetails;
import com.example.invenza.dto.LoginRequest;
import com.example.invenza.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @GetMapping("/test")
    public String test() {
        return "Hello, this is a test endpoint!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        var token = new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());
        Authentication auth;

        try {
            auth = authenticationManager.authenticate(token);
        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid account or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        var user = (MemberUserDetails) auth.getPrincipal();
        var jwt = jwtService.createLoginAccessToken(user);

        return ResponseEntity.ok(LoginResponse.of(jwt, user));
    }

    @GetMapping("/who-am-i")
    public ResponseEntity<?> whoAmI() {
        // String jwt = jwt from security context 
        String jwt = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        var user = (MemberUserDetails) auth.getPrincipal();
        var response = LoginResponse.of(jwt, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/home")
    public String home() {
        return "This is home page. 登入後可見";
    }
}
