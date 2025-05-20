package com.example.invenza.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invenza.config.JwtService;
import com.example.invenza.config.MemberUserDetails;
import com.example.invenza.dto.LoginRequest;
import com.example.invenza.dto.LoginResponse;

@RestController
public class TestController {
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @RequestMapping("/test")
    public String test() {
        return "Hello, this is a test endpoint!";
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var auth = authenticationManager.authenticate(token);
        var user = (MemberUserDetails) auth.getPrincipal();

        var jwt = jwtService.createLoginAccessToken(user);

        return LoginResponse.of(jwt, user);
    }

    @GetMapping("/who-am-i")
    public LoginResponse whoAmI() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("未登入");
        }
        var user = (MemberUserDetails) auth.getPrincipal();
        return LoginResponse.of(null, user); // 不回傳 jwt，只回傳 user 資訊
    }

    @GetMapping("/home")
    public String home() {
        return "This is home page. 登入後可見";
    }
}
