package com.example.userpost.controller;

import com.example.userpost.dto.user.UserDto;
import com.example.userpost.dto.user.UserListDto;
import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final IUserService userService;

  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String keyword) {
    List<UserDto> users;
    if (keyword == null || keyword.isBlank()) {
      users = userService.getAllUsers();
    } else {
      users = userService.searchUsers(keyword);
    }
    return ResponseBuilder.success(new UserListDto(users.size(), users));
  }
}

