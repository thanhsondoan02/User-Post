package com.example.userpost.service;

import com.example.userpost.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
  List<User> getAllUsers();

  List<User> searchUsers(String key);

  User getUserByUsername(String username);

  User getUserByEmail(String email);

  boolean isUsernameExist(String username);

  boolean isEmailExist(String email);

  boolean isUserIdExist(String id);

  boolean isUserIdExistAndActive(String id);
}
