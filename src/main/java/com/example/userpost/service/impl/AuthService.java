package com.example.userpost.service.impl;

import com.example.userpost.constant.Gender;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.dto.request.auth.ChangePasswordRequestDto;
import com.example.userpost.dto.request.auth.LoginRequestDto;
import com.example.userpost.dto.request.auth.RegisterRequestDto;
import com.example.userpost.dto.response.auth.JwtResponseDto;
import com.example.userpost.model.openid.AcceptedConnection;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IAuthService;
import com.example.userpost.util.ValidationUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  public JwtResponseDto register(RegisterRequestDto request) {
    String username = request.getUsername();
    String password = request.getPassword();

    String hashedPassword = passwordEncoder.encode(password);

    Gender gender;
    if (request.getGender() != null) {
      gender = Gender.fromCode(request.getGender());
    } else {
      gender = null;
    }

    var user = new User(username, request.getEmail(), hashedPassword, request.getFullName(), gender, request.getDateOfBirth());
    userRepository.save(user);

    return login(username, password);
  }

  @Override
  public JwtResponseDto login(String username, String password) {
    var authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(username, password));

    var user = (User) authentication.getPrincipal();

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtils.generateToken(username, password, user.getRole());
    return new JwtResponseDto(token);
  }

  @Override
  public JwtResponseDto loginOpenId(String clientId, String clientSecret) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(clientId, clientSecret));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtils.generateTokenOpenid(clientId, clientSecret);
    return new JwtResponseDto(token);
  }

  @Override
  public void changePassword(String newPassword) {
    var user = getAuthUser();
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  @Override
  public boolean validateRegisterFields(RegisterRequestDto request) {
    return request.getUsername() != null && request.getEmail() != null && request.getPassword() != null;
  }

  @Override
  public boolean validateRegisterFormat(RegisterRequestDto request) {
    return ValidationUtils.isUsernameValid(request.getUsername())
      && ValidationUtils.isEmailValid(request.getEmail())
      && ValidationUtils.isPasswordValid(request.getPassword())
      && (request.getGender() == null || ValidationUtils.isGenderValid(request.getGender()))
      && (request.getDateOfBirth() == null || ValidationUtils.isDateOfBirthValid(request.getDateOfBirth()));
  }

  @Override
  public boolean validateLoginFields(LoginRequestDto request) {
    return request.getEmail() != null && request.getPassword() != null;
  }

  @Override
  public boolean validateLoginFormat(LoginRequestDto request) {
    return ValidationUtils.isEmailValid(request.getEmail())
      && ValidationUtils.isPasswordValid(request.getPassword());
  }

  @Override
  public boolean validateChangePasswordFields(ChangePasswordRequestDto request) {
    return request.getOldPassword() != null && request.getNewPassword() != null;
  }

  @Override
  public boolean validateChangePasswordFormat(ChangePasswordRequestDto request) {
    return ValidationUtils.isPasswordValid(request.getOldPassword())
      && ValidationUtils.isPasswordValid(request.getNewPassword());
  }

  @Override
  public boolean isTruePassword(String password) {
    return passwordEncoder.matches(password, getAuthUser().getPasswordHash());
  }

  @Override
  public User getAuthUser() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return (User) authentication.getPrincipal();
  }

  @Override
  public AcceptedConnection getAuthClient() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return (AcceptedConnection) authentication.getPrincipal();
  }

  @Override
  public SecurityRole getAuthRole() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getAuthorities().stream()
      .findFirst()
      .map(authority -> {
        var authorityName = authority.getAuthority();
        if (authorityName.startsWith("ROLE_")) {
          return SecurityRole.fromString(authorityName.substring(5));
        } else {
          throw new IllegalStateException("Authority does not start with 'ROLE_' prefix: " + authorityName);
        }
      })
      .orElseThrow(() -> new IllegalStateException("No role found in authentication context"));
  }
}
