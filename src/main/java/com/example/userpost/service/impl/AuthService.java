package com.example.userpost.service.impl;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.security.JwtUtils;
import com.example.userpost.service.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Pattern;

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

    return login(new LoginRequest(username, password));
  }

  @Override
  public JwtInfo login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());
    return new JwtInfo(token);
  }

  @Override
  public String changePassword(ChangePasswordRequest request) {
    return null;
  }

  @Override
  public boolean validateRequiredFields(RegisterRequest request) {
    return request.getUsername() != null && request.getEmail() != null && request.getPassword() != null;
  }

  @Override
  public boolean validateInputFormat(RegisterRequest request) {
    final String username = request.getUsername();
    if (username == null || username.length() < 6 || username.length() > 20) return false;

    String email = request.getEmail();
    Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    if (email == null || !emailRegex.matcher(email).matches()) return false;

    final String password = request.getPassword();
    String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,20}$";
    if (password == null || !password.matches(passwordRegex)) return false;

    final String gender = request.getGender();
    if (gender != null && !(gender.equals("M") || gender.equals("F"))) return false;

    final LocalDate dateOfBirth = request.getDateOfBirth();
    if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) return false;

    return true;
  }

  @Override
  public boolean isUsernameExist(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  @Override
  public boolean isEmailExist(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  @Override
  public boolean validateRequiredFields(LoginRequest request) {
    return request.getUsername() != null && request.getPassword() != null;
  }
}
