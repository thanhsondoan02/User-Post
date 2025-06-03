package com.example.userpost.controller;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.auth.ChangePasswordRequestDto;
import com.example.userpost.dto.request.auth.LoginRequestDto;
import com.example.userpost.dto.request.auth.RefreshTokenRequestDto;
import com.example.userpost.dto.request.auth.RegisterRequestDto;
import com.example.userpost.dto.request.openid.auth.OpenIdLoginRequestDto;
import com.example.userpost.dto.response.user.UserResponseDto;
import com.example.userpost.service.IApiService;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final IAuthService authService;
  private final IUserService userService;
  private final IApiService apiService;

  public AuthController(IAuthService authService, IUserService userService, IApiService apiService) {
    this.authService = authService;
    this.userService = userService;
    this.apiService = apiService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
    if (!authService.validateRegisterFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!authService.validateRegisterFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else if (userService.isUsernameExist(request.getUsername()) || userService.isEmailExist(request.getEmail())) {
      return ResponseBuilder.error(HttpStatus.CONFLICT.value(), MessageConst.EMAIL_OR_USERNAME_EXISTS);
    } else {
      var res = authService.register(request);
      apiService.sendWebhookScopeUsers(HookEvent.CREATE, new UserResponseDto(authService.getAuthUser()));
      return ResponseBuilder.build(HttpStatus.CREATED.value(), MessageConst.REGISTER_SUCCESS, res);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
    if (!authService.validateLoginFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!authService.validateLoginFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else {
      var user = userService.getUserByEmail(request.getEmail());
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

  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto request) {
    try {
      return ResponseBuilder.success(authService.refreshToken(request.getRefreshToken()));
    } catch (BadCredentialsException e) {
      return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), MessageConst.INVALID_REFRESH_TOKEN);
    }
  }

  @PostMapping("/login-openid")
  public ResponseEntity<?> loginOpenId(@RequestBody OpenIdLoginRequestDto request) {
    var clientId = request.getClientId();
    var clientSecret = request.getClientSecret();

    if (clientId == null || clientSecret == null) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    }

    try {
      return ResponseBuilder.success(authService.loginOpenId(clientId, clientSecret));
    } catch (BadCredentialsException e) {
      return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), MessageConst.INVALID_CLIENT_ID_OR_CLIENT_SECRET);
    }
  }

  @PutMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto request) {
    var oldPassword = request.getOldPassword();
    var newPassword = request.getNewPassword();
    if (!authService.validateChangePasswordFields(request)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.MISSING_REQUIRED_FIELD);
    } else if (!authService.validateChangePasswordFormat(request)) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_INPUT_FORMAT);
    } else if (!authService.isTruePassword(oldPassword)) {
      return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), MessageConst.INVALID_OLD_PASSWORD);
    } else if (newPassword.equals(oldPassword)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.NEW_PASSWORD_SAME_AS_OLD);
    } else {
      authService.changePassword(request.getNewPassword());
      return ResponseBuilder.build(HttpStatus.OK.value(), MessageConst.PASSWORD_CHANGED_SUCCESS, null);
    }
  }
}
