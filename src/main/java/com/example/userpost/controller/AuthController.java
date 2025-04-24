package com.example.userpost.controller;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.service.IAuthService;
import com.example.userpost.util.MessageConst;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
    if (!authService.validateRegisterFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELDS);
    } else if (!authService.validateRegisterFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else if (authService.isUsernameExist(request.getUsername()) || authService.isEmailExist(request.getEmail())) {
      return ResponseBuilder.error(HttpStatus.CONFLICT.value(), MessageConst.EMAIL_OR_USERNAME_EXISTS);
    } else {
      return ResponseBuilder.build(HttpStatus.CREATED.value(), MessageConst.REGISTER_SUCCESS, authService.register(request));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    if (!authService.validateLoginFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELDS);
    } else if (!authService.validateLoginFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else {
      var user = authService.getUserByEmail(request.getEmail());
      if (user == null) {
        return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), MessageConst.INVALID_EMAIL_OR_PASSWORD);
      } else {
        var username = user.getUsername();
        try {
          return ResponseBuilder.success(authService.login(username, request.getPassword()));
        } catch (BadCredentialsException e) {
          return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), MessageConst.INVALID_EMAIL_OR_PASSWORD);
        }
      }
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
    return ResponseBuilder.success(authService.changePassword(request));
  }
}

