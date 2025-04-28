package com.example.userpost.service;

import com.example.userpost.dto.response.user.UserListResponseDto;
import com.example.userpost.model.user.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
  UserListResponseDto getAllUsers();

  UserListResponseDto searchUsers(String key);

  User getUserByUsername(String username);

  User getUserByEmail(String email);

  boolean isUsernameExist(String username);

  boolean isEmailExist(String email);

  boolean isUserIdExist(String id);
}
