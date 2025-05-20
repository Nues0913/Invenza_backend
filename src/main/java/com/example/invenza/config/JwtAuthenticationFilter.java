package com.example.invenza.config;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private static final List<String> WHITELIST = List.of(
        "/api/auth/login",
        "/api/auth/test",
        "/api/auth/forgotpassword"
    );
    
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (WHITELIST.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(BEARER_PREFIX.length());
        try {
            Claims claims = jwtService.parseToken(jwtToken);
            setUpSecurityContext(claims);
        } catch (JwtException e) {
            handleAuthenticationError(response, "Invalid Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setUpSecurityContext(Claims claims) {
        MemberUserDetails userDetails = new MemberUserDetails();
        userDetails.setId(claims.getSubject());
        userDetails.setUsername(claims.get("username", String.class));
        userDetails.setEmail(claims.get("email", String.class));
        userDetails.setPhone(claims.get("phone", String.class));

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void handleAuthenticationError(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", errorMessage));
    }
}