package com.example.userpost.service;

import com.example.userpost.constant.SecurityRole;
import com.example.userpost.dto.request.auth.ChangePasswordRequestDto;
import com.example.userpost.dto.request.auth.LoginRequestDto;
import com.example.userpost.dto.request.auth.RegisterRequestDto;
import com.example.userpost.dto.response.auth.ServerJwtResponseDto;
import com.example.userpost.dto.response.auth.UserJwtResponseDto;
import com.example.userpost.model.openid.Connection;
import com.example.userpost.model.user.User;

public interface IAuthService {
  UserJwtResponseDto register(RegisterRequestDto request);

  UserJwtResponseDto login(String username, String password);

  UserJwtResponseDto refreshToken(String refreshToken);

  ServerJwtResponseDto loginOpenId(String clientId, String clientSecret);

  ServerJwtResponseDto refreshOpenIdToken(String refreshToken);

  void changePassword(String newPassword);

  boolean validateRegisterFields(RegisterRequestDto request);

  boolean validateRegisterFormat(RegisterRequestDto request);

  boolean validateLoginFields(LoginRequestDto request);

  boolean validateLoginFormat(LoginRequestDto request);

  boolean validateChangePasswordFields(ChangePasswordRequestDto request);

  boolean validateChangePasswordFormat(ChangePasswordRequestDto request);

  boolean isTruePassword(String password);

  User getAuthUser();

  Connection getAuthClient();

  SecurityRole getAuthRole();
}
