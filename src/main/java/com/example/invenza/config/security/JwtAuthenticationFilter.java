package com.example.invenza.config.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.invenza.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private static final List<String> WHITELIST = List.of(Constants.PUBLIC_API_PATHS);
    
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
            handleAuthenticationError(HttpServletResponse.SC_UNAUTHORIZED, response, Constants.MISSING_OR_WRONG_HEADER_MESSAGE);
            return;
        }

        String jwtToken = authHeader.substring(BEARER_PREFIX.length());
        try {
            Claims claims = jwtService.parseToken(jwtToken);
            setUpSecurityContext(claims, jwtToken);
        } catch (JwtException e) {
            handleAuthenticationError(HttpServletResponse.SC_UNAUTHORIZED, response, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            handleAuthenticationError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response, e.getMessage());
            return;
        } catch (Exception e) {
            handleAuthenticationError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setUpSecurityContext(Claims claims, String jwtToken) throws Exception{
        if (claims == null) {
            throw new IllegalArgumentException("null Claims");
        }

        try {
            MemberUserDetails userDetails = new MemberUserDetails();
            userDetails.setId(claims.getSubject());
            userDetails.setName(claims.get("name", String.class));
            userDetails.setEmail(claims.get("email", String.class));
            userDetails.setPhone(claims.get("phone", String.class));
    
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    jwtToken,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            throw e;
        }
    }

    private void handleAuthenticationError(int statusCode, HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", errorMessage));
    }
}