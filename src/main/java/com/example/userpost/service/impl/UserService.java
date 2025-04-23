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
  public UserDetails loadUserDetailByUsername(String username) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new org.springframework.security.core.userdetails.User(
      user.getUsername(), user.getPasswordHash(), new ArrayList<>());
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
}
