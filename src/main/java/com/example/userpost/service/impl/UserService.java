package com.example.userpost.service.impl;

import com.example.userpost.constant.State;
import com.example.userpost.dto.response.user.UserListResponseDto;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
  UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserListResponseDto getAllUsers() {
    return new UserListResponseDto(userRepository.findAllByState(State.ACTIVE));
  }

  @Override
  public UserListResponseDto searchUsers(String key) {
    List<User> users = userRepository.searchByKeyword(key);
    return new UserListResponseDto(users);
  }

  @Override
  public User getUserByUsername(String username) {
    return userRepository.findByUsernameAndState(username, State.ACTIVE).orElse(null);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmailAndState(email, State.ACTIVE).orElse(null);
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
