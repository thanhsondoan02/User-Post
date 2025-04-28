package com.example.userpost.dto.response.user;

import com.example.userpost.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserListResponseDto {
  private int total;
  private List<UserResponseDto> users;

  public UserListResponseDto(List<User> users) {
    this.total = users.size();
    this.users = users.stream()
      .map(UserResponseDto::new)
      .toList();
  }
}
