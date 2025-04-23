package com.example.userpost.service;

import com.example.userpost.dto.user.UserListResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
  UserDetails loadUserDetailByUsername(String username);
  UserListResponse getAllUsers();
  UserListResponse searchUsers(String key);
  UserListResponse getUserById(String id);
}
