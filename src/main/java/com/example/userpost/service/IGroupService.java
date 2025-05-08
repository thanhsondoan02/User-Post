package com.example.userpost.service;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.CreateGroupResponseDto;

public interface IGroupService {
  CreateGroupResponseDto createGroup(CreateGroupRequestDto request);
  boolean isGroupExistAndActive(String id);
  boolean isInGroup(String userId, String groupId);
  boolean isGroupAdmin(String userId, String groupId);
  void updateGroup(String groupId, UpdateGroupRequestDto request);
}
