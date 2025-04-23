package com.example.userpost.dto.user;

import com.example.userpost.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserListResponse {
  private List<User> users;
}
