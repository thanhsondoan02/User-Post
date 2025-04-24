package com.example.userpost.service.impl;

import com.example.userpost.dto.user.UserListResponse;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.service.IUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements IUserService {
  UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserListResponse getAllUsers() {
    return new UserListResponse(userRepository.findAll());
  }

  @Override
  public UserListResponse searchUsers(String key) {
    return null;
  }

  @Override
  public UserListResponse getUserById(String id) {
    return null;
  }

  @Override
  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Override
  public boolean isUsernameExist(String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public boolean isEmailExist(String email) {
    return userRepository.existsByEmail(email);
  }
}
