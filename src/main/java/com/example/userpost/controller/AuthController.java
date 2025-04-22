package com.example.userpost.controller;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.service.IAuthService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  IAuthService authService;

  public AuthController(IAuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    return ResponseBuilder.success(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    return ResponseBuilder.success(authService.login(request));
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
    return ResponseBuilder.success(authService.changePassword(request));
  }
}

