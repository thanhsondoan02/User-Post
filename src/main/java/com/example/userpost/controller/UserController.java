package com.example.userpost.controller;

import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final IUserService userService;

  public UserController(IUserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<?> getAllUsers() {
    return ResponseBuilder.success(userService.getAllUsers());
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
    return ResponseBuilder.success(userService.searchUsers(keyword));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable String id) {
    return ResponseBuilder.success(userService.getUserById(id));
  }
}

