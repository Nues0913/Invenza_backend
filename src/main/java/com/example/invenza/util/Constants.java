package com.example.invenza.util;

public class Constants {
    
    public static final String[] PUBLIC_API_PATHS = {
        "/api/auth/login",
        "/api/auth/test",
        "/api/auth/forgot-password"
    };
    
    public static final String LOGIN_FAILED_MESSAGE = "Login failed, check your account and password.";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid Token";
    public static final String MISSING_OR_WRONG_HEADER_MESSAGE = "Missing or wrong Authorization header";
    public static final String MISSING_EMAIL_MESSAGE = "Email is required";
    public static final String UNKNOWN_ERROR_MESSAGE = "Unknown error, please try again later";

}
