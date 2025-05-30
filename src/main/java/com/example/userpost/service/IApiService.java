package com.example.userpost.service;


import com.example.userpost.constant.HookEvent;
import com.example.userpost.dto.request.openid.connect.UpdateConnectionRequestDto;
import com.example.userpost.dto.response.group.GroupResponseDto;
import com.example.userpost.dto.response.post.PostResponseDto;
import com.example.userpost.dto.response.user.UserResponseDto;

public interface IApiService {
  void updateConnection(String url, UpdateConnectionRequestDto body);

  void sendWebhookScopeUsers(HookEvent eventType, UserResponseDto data);

  void sendWebhookScopeGroups(HookEvent eventType, GroupResponseDto data);

  void sendWebhookScopeGroupMembers(HookEvent eventType, GroupResponseDto data);

  void sendWebhookScopePosts(HookEvent eventType, PostResponseDto data);
}
