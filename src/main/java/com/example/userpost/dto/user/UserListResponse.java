package com.example.userpost.dto.user;

import com.example.userpost.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserListResponse {
  private List<User> users;
}
