package com.example.userpost.service.impl;

import com.example.userpost.dto.user.UserDto;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.service.IUserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
  UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<UserDto> getAllUsers() {
    return convertToListUserDto(userRepository.findAll());
  }

  @Override
  public List<UserDto> searchUsers(String key) {
    List<User> users = userRepository.findByUsernameIgnoreCaseContainingOrFullNameIgnoreCaseContaining(key, key);
    return convertToListUserDto(users);
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

  private List<UserDto> convertToListUserDto(List<User> users) {
    return users.stream()
      .map(this::convertToUserDto)
      .toList();
  }

  private UserDto convertToUserDto(User user) {
    var builder = UserDto.builder()
      .id(user.getId())
      .username(user.getUsername())
      .email(user.getEmail())
      .fullName(user.getFullName())
      .dateOfBirth(user.getDateOfBirth());
    var gender = user.getGender();
    if (gender != null) {
      builder.gender(gender.toString());
    }
    return builder.build();
  }
}
