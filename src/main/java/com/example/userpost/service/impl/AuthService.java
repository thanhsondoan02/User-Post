package com.example.userpost.service.impl;

import com.example.userpost.dto.auth.ChangePasswordRequest;
import com.example.userpost.dto.auth.JwtInfo;
import com.example.userpost.dto.auth.LoginRequest;
import com.example.userpost.dto.auth.RegisterRequest;
import com.example.userpost.dto.base.BaseResponse;
import com.example.userpost.exception.BadRequestException;
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

    // Check if the username already exists
    if (userRepository.findByUsername(username).isPresent()) {
      throw new BadRequestException("Username already exists");
    }

    // Hash the password before saving it
    String hashedPassword = passwordEncoder.encode(password);

    // Save the user in the repository (database)
    User user = new User();
    user.setUsername(username);
    user.setPasswordHash(hashedPassword);
    user.setEmail(request.getEmail());
    user.setFullName(request.getFullName());
    userRepository.save(user);

    // After registration, login the user and generate a JWT token
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
}
