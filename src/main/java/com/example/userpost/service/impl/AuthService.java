package com.example.userpost.service.impl;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IAuthService;
import com.example.userpost.util.ValidationUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public JwtInfo register(RegisterRequest request) {
    String username = request.getUsername();
    String password = request.getPassword();

    String hashedPassword = passwordEncoder.encode(password);

    User.Gender gender;
    try {
      gender = User.Gender.valueOf(request.getGender().toUpperCase());
    } catch (Exception e) {
      gender = null;
    }

    var user = User.builder()
      .username(username)
      .email(request.getEmail())
      .passwordHash(hashedPassword)
      .fullName(request.getFullName())
      .gender(gender)
      .dateOfBirth(request.getDateOfBirth())
      .build();
    userRepository.save(user);

    return login(username, password);
  }

  @Override
  public JwtInfo login(String username, String password) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(username, password));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());
    return new JwtInfo(token);
  }

  @Override
  public void changePassword(String newPassword) {
    var user = getAuthUser();
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Override
  public boolean validateRegisterFields(RegisterRequest request) {
    return request.getUsername() != null && request.getEmail() != null && request.getPassword() != null;
  }

  @Override
  public boolean validateRegisterFormat(RegisterRequest request) {
    return ValidationUtils.isUsernameValid(request.getUsername())
      && ValidationUtils.isEmailValid(request.getEmail())
      && ValidationUtils.isPasswordValid(request.getPassword())
      && (request.getGender() == null || ValidationUtils.isGenderValid(request.getGender()))
      && (request.getDateOfBirth() == null || ValidationUtils.isDateOfBirthValid(request.getDateOfBirth()));
  }

  @Override
  public boolean validateLoginFields(LoginRequest request) {
    return request.getEmail() != null && request.getPassword() != null;
  }

  @Override
  public boolean validateLoginFormat(LoginRequest request) {
    return ValidationUtils.isEmailValid(request.getEmail())
      && ValidationUtils.isPasswordValid(request.getPassword());
  }

  @Override
  public boolean validateChangePasswordFields(ChangePasswordRequest request) {
    return request.getOldPassword() != null && request.getNewPassword() != null;
  }

  @Override
  public boolean validateChangePasswordFormat(ChangePasswordRequest request) {
    return ValidationUtils.isPasswordValid(request.getOldPassword())
      && ValidationUtils.isPasswordValid(request.getNewPassword());
  }

  @Override
  public boolean isTruePassword(String password) {
    return passwordEncoder.matches(password, getAuthUser().getPasswordHash());
  }

  private User getAuthUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }
}
