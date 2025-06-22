package com.example.invenza.controller;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.dto.LoginRequest;
import com.example.invenza.dto.LoginResponse;
import com.example.invenza.service.AuthService;
import com.example.invenza.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/test")
    public String test() {
        return "Hello, this is a test endpoint!";
    }

    @GetMapping("/who-am-i")
    public ResponseEntity<?> whoAmI() {
        try {
            log.debug("/who-am-i called");
            var response = authService.getCurrentUser();
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.error("/who-am-i {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", Constants.INVALID_TOKEN_MESSAGE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("/who-am-i {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", Constants.UNKNOWN_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.debug("/login called with request: {}", request);
            LoginResponse response = authService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.debug("/login {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", Constants.LOGIN_FAILED_MESSAGE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.warn("/login {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", Constants.UNKNOWN_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        log.debug("/forgot-password called with request: {}", request);
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.MISSING_EMAIL_MESSAGE);
        }
        try {
            // TODO: implement forgotPassword in AuthService
            authService.forgotPassword(email);
            return ResponseEntity.ok(email);
        } catch (RuntimeException e) {
            log.debug("/forgot-password {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            log.warn("/forgot-password {}: {}", e.getClass().getName(), e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", Constants.UNKNOWN_ERROR_MESSAGE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
