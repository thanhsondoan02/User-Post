package com.example.userpost.service;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;

public interface IAuthService {
  JwtInfo register(RegisterRequest request);

  JwtInfo login(String username, String password);

  void changePassword(String newPassword);

  boolean validateRegisterFields(RegisterRequest request);

  boolean validateRegisterFormat(RegisterRequest request);

  boolean validateLoginFields(LoginRequest request);

  boolean validateLoginFormat(LoginRequest request);

  boolean validateChangePasswordFields(ChangePasswordRequest request);

  boolean validateChangePasswordFormat(ChangePasswordRequest request);

  boolean isTruePassword(String password);
}
