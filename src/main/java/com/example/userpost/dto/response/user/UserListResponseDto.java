package com.example.userpost.dto.response.user;

import com.example.userpost.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserListResponseDto {
  private int total;
  private List<UserResponseDto> users;

  public UserListResponseDto(List<User> users) {
    this.total = users.size();
    this.users = users.stream()
      .map(UserResponseDto::new)
      .toList();
  }

  public static UserListResponseDto cloneFromUsers(List<User> users) {
    var dto = new UserListResponseDto();
    dto.setTotal(users.size());
    dto.setUsers(users.stream()
      .map(UserResponseDto::cloneFromUser)
      .toList());
    return dto;
  }
}
