package com.example.userpost.controller;

import com.example.userpost.constant.GroupRole;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.GroupUserRequestDto;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IGroupService;
import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import com.example.userpost.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final IGroupService groupService;
  private final IAuthService authService;
  private final IUserService userService;

  public GroupController(IGroupService groupService, IAuthService authService, IUserService userService) {
    this.groupService = groupService;
    this.authService = authService;
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequestDto request) {
    if (!ValidationUtils.isGroupNameValid(request.getName())) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.INVALID_INPUT_FORMAT);
    }

    List<GroupUserRequestDto> users = new ArrayList<>();
    // Add the authenticated user as an admin
    users.add(new GroupUserRequestDto(authService.getAuthUser().getId(), GroupRole.ADMIN.toString()));
    // Add other users from the request
    if (request.getUsers() != null) {
      users.addAll(request.getUsers());
    }

    // Check if users have any duplicate user IDs
    if (users.size() > 1) {
      var userIds = users.stream().map(GroupUserRequestDto::getUserId).toList();
      if (userIds.size() != userIds.stream().distinct().count()) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.DUPLICATE_USER_ID);
      }
    }

    // Check if users' role is valid
    for (var userRole : users) {
      try {
        userRole.getRole();
      } catch (IllegalArgumentException e) {
        return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_ROLE);
      }
    }

    // Check if users exist
    for (var userRole : users) {
      if (!userService.isUserIdExist(userRole.getUserId())) {
        return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.USER_NOT_FOUND);
      }
    }

    request.setUsers(users);
    return ResponseBuilder.success(groupService.createGroup(request));
  }

//  @GetMapping
//  public ResponseEntity<?> searchPost(@RequestParam(required = false) String title, @RequestParam(required = false) String userId) {
//  }
//
//  @PutMapping("/{id}")
//  public ResponseEntity<?> updatePost(@PathVariable("id") String id, @RequestBody UpdatePostRequestDto request) {
//
//  }
//
//  @DeleteMapping("/{id}")
//  public ResponseEntity<?> deletePost(@PathVariable("id") String id) {
//
//  }
}
