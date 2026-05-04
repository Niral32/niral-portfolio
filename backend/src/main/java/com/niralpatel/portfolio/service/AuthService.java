package com.niralpatel.portfolio.service;

import com.niralpatel.portfolio.config.AdminProperties;
import com.niralpatel.portfolio.config.JwtProperties;
import com.niralpatel.portfolio.dto.LoginRequest;
import com.niralpatel.portfolio.dto.LoginResponse;
import com.niralpatel.portfolio.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AdminProperties adminProperties;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(AdminProperties adminProperties, JwtService jwtService, JwtProperties jwtProperties) {
        this.adminProperties = adminProperties;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    public LoginResponse login(LoginRequest request) {
        if (!adminProperties.username().equals(request.username())
                || !adminProperties.password().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.generateToken(request.username());
        return new LoginResponse(token, "Bearer", jwtProperties.expirationMs());
    }
}
