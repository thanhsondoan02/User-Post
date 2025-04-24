package com.example.userpost.service;

import com.example.userpost.dto.user.UserListResponse;
import com.example.userpost.model.user.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
  UserListResponse getAllUsers();

  UserListResponse searchUsers(String key);

  UserListResponse getUserById(String id);

  User getUserByUsername(String username);

  User getUserByEmail(String email);

  boolean isUsernameExist(String username);

  boolean isEmailExist(String email);
}
