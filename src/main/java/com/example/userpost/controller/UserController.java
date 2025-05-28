package com.example.userpost.controller;

import com.example.userpost.constant.SecurityRole;
import com.example.userpost.dto.response.user.UserListResponseDto;
import com.example.userpost.model.user.User;
import com.example.userpost.service.IAuthService;
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
  private final IAuthService authService;

  public UserController(IUserService userService, IAuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String keyword) {
    UserListResponseDto response;
    if (authService.getAuthRole() == SecurityRole.CLIENT) {
      var users = userService.getAllUsers();
      response = UserListResponseDto.cloneFromUsers(users);
    } else {
      List<User> users;
      if (keyword == null || keyword.isBlank()) {
        users = userService.getAllUsers();
      } else {
        users = userService.searchUsers(keyword);
      }
      response = new UserListResponseDto(users);
    }
    return ResponseBuilder.success(response);
  }
}

