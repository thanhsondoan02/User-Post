package com.example.userpost.service;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;

public interface IAuthService {
  JwtInfo register(RegisterRequest request);

  JwtInfo login(LoginRequest request);

  String changePassword(ChangePasswordRequest request);
}
