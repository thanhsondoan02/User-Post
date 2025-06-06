package com.example.userpost.controller;

import com.example.userpost.constant.GroupRole;
import com.example.userpost.constant.HookEvent;
import com.example.userpost.constant.MessageConst;
import com.example.userpost.constant.SecurityRole;
import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.GroupUserRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.GroupListResponseDto;
import com.example.userpost.dto.response.group.GroupResponseDto;
import com.example.userpost.dto.response.group.GroupUserResponseDto;
import com.example.userpost.service.IApiService;
import com.example.userpost.service.IAuthService;
import com.example.userpost.service.IGroupService;
import com.example.userpost.service.IUserService;
import com.example.userpost.util.ResponseBuilder;
import com.example.userpost.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final IGroupService groupService;
  private final IAuthService authService;
  private final IUserService userService;
  private final IApiService apiService;

  public GroupController(IGroupService groupService, IAuthService authService, IUserService userService, IApiService apiService) {
    this.groupService = groupService;
    this.authService = authService;
    this.userService = userService;
    this.apiService = apiService;
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
      if (userRole.getRole() == null) {
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
    var res = groupService.createGroup(request);

    // Send webhooks
    apiService.sendWebhookScopeGroups(HookEvent.CREATE, res);

    return ResponseBuilder.success();
  }

  @PutMapping("/{groupId}")
  public ResponseEntity<?> updateGroupInfo(@PathVariable("groupId") String groupId, @RequestBody UpdateGroupRequestDto request) {
    var name = request.getName();
    if (name != null && !ValidationUtils.isGroupNameValid(name)) {
      return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), MessageConst.INVALID_INPUT_FORMAT);
    }
    if (!groupService.isGroupExistAndActive(groupId)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.GROUP_NOT_FOUND);
    }
    if (!groupService.isInGroup(authService.getAuthUser().getId(), groupId)) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }

    groupService.updateGroup(groupId, request);

    // Send webhooks
    var dto = new GroupResponseDto();
    dto.setId(groupId);
    dto.setName(request.getName());
    dto.setThumbnail(request.getThumbnail());
    apiService.sendWebhookScopeGroups(HookEvent.UPDATE, dto);

    return ResponseBuilder.success(null);
  }

  @DeleteMapping("/{groupId}")
  public ResponseEntity<?> deleteGroup(@PathVariable("groupId") String groupId) {
    if (!groupService.isGroupExistAndActive(groupId)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.GROUP_NOT_FOUND);
    }
    if (!groupService.isGroupAdmin(authService.getAuthUser().getId(), groupId)) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }

    groupService.deleteGroup(groupId);

    // Send webhooks
    var dto = new GroupResponseDto();
    dto.setId(groupId);
    apiService.sendWebhookScopeGroups(HookEvent.DELETE, dto);

    return ResponseBuilder.success();
  }

  @PostMapping("/{groupId}/members")
  public ResponseEntity<?> addUserToGroup(@PathVariable("groupId") String groupId, @RequestBody GroupUserRequestDto request) {
    if (request.getRole() == null) {
      return ResponseBuilder.error(HttpStatus.UNPROCESSABLE_ENTITY.value(), MessageConst.INVALID_ROLE);
    }
    if (!groupService.isGroupExistAndActive(groupId)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.GROUP_NOT_FOUND);
    }
    if (!groupService.isGroupAdmin(authService.getAuthUser().getId(), groupId)) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }
    if (!userService.isUserIdExistAndActive(request.getUserId())) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.USER_NOT_FOUND);
    }
    if (groupService.isInGroup(request.getUserId(), groupId)) {
      return ResponseBuilder.error(HttpStatus.CONFLICT.value(), MessageConst.USER_ALREADY_IN_GROUP);
    }

    groupService.addUserToGroup(groupId, request);

    // Send webhooks
    var user = new GroupUserResponseDto();
    user.setUserId(request.getUserId());
    user.setRole(request.getRole().toString());
    var dto = new GroupResponseDto();
    dto.setId(groupId);
    dto.setUsers(List.of(user));
    apiService.sendWebhookScopeGroupMembers(HookEvent.CREATE, dto);

    return ResponseBuilder.success();
  }

  @DeleteMapping("/{groupId}/members/{userId}")
  public ResponseEntity<?> deleteUserFromGroup(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId) {
    if (!groupService.isGroupExistAndActive(groupId)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.GROUP_NOT_FOUND);
    }
    if (!groupService.isGroupAdmin(authService.getAuthUser().getId(), groupId)) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }
    if (!groupService.isInGroup(userId, groupId)) {
      return ResponseBuilder.error(HttpStatus.CONFLICT.value(), MessageConst.USER_NOT_FOUND);
    }
    groupService.removeUserFromGroup(groupId, userId);

    // Send webhooks
    var user = new GroupUserResponseDto();
    user.setUserId(userId);
    var dto = new GroupResponseDto();
    dto.setId(groupId);
    dto.setUsers(List.of(user));
    apiService.sendWebhookScopeGroupMembers(HookEvent.DELETE, dto);

    return ResponseBuilder.success();
  }

  @GetMapping()
  public ResponseEntity<?> getGroupList() {
    if (authService.getAuthRole() == SecurityRole.CLIENT) {
      var groups = groupService.getAll();
      return ResponseBuilder.success(new GroupListResponseDto(groups, false));
    } else {
      var userId = authService.getAuthUser().getId();
      var groups = groupService.getGroupList(userId);
      return ResponseBuilder.success(new GroupListResponseDto(groups, false));
    }
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<?> getGroupInfo(@PathVariable("groupId") String groupId) {
    var userId = authService.getAuthUser().getId();
    if (!groupService.isGroupExistAndActive(groupId)) {
      return ResponseBuilder.error(HttpStatus.NOT_FOUND.value(), MessageConst.GROUP_NOT_FOUND);
    }
    if (!groupService.isInGroup(userId, groupId)) {
      return ResponseBuilder.error(HttpStatus.FORBIDDEN.value(), MessageConst.ACCESS_DENIED);
    }
    return ResponseBuilder.success(groupService.getGroupInfo(groupId));
  }
}
