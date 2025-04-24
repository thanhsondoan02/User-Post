package com.example.userpost.service;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.model.user.User;

public interface IAuthService {
  JwtInfo register(RegisterRequest request);

  JwtInfo login(String username, String password);

  String changePassword(ChangePasswordRequest request);

  boolean validateRegisterFields(RegisterRequest request);

  boolean validateRegisterFormat(RegisterRequest request);

  boolean isUsernameExist(String username);

  boolean isEmailExist(String email);

  boolean validateLoginFields(LoginRequest request);

  boolean validateLoginFormat(LoginRequest request);

  User getUserByEmail(String email);
}
