package com.example.userpost.service;

import com.example.userpost.dto.request.auth.ChangePasswordRequestDto;
import com.example.userpost.dto.request.auth.LoginRequestDto;
import com.example.userpost.dto.request.auth.RegisterRequestDto;
import com.example.userpost.dto.response.auth.JwtResponseDto;
import com.example.userpost.model.user.User;

public interface IAuthService {
  JwtResponseDto register(RegisterRequestDto request);

  JwtResponseDto login(String username, String password);

  void changePassword(String newPassword);

  boolean validateRegisterFields(RegisterRequestDto request);

  boolean validateRegisterFormat(RegisterRequestDto request);

  boolean validateLoginFields(LoginRequestDto request);

  boolean validateLoginFormat(LoginRequestDto request);

  boolean validateChangePasswordFields(ChangePasswordRequestDto request);

  boolean validateChangePasswordFormat(ChangePasswordRequestDto request);

  boolean isTruePassword(String password);

  User getAuthUser();

  boolean isAdmin();
}
