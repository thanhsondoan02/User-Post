package com.example.userpost.service;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.GroupUserRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.GroupListResponseDto;
import com.example.userpost.dto.response.group.GroupResponseDto;

public interface IGroupService {
  GroupResponseDto createGroup(CreateGroupRequestDto request);
  boolean isGroupExistAndActive(String id);
  boolean isInGroup(String userId, String groupId);
  boolean isGroupAdmin(String userId, String groupId);
  void updateGroup(String groupId, UpdateGroupRequestDto request);
  void deleteGroup(String groupId);
  void addUserToGroup(String groupId, GroupUserRequestDto request);
  void removeUserFromGroup(String groupId, String userId);
  GroupListResponseDto getGroupList(String userId);
  GroupResponseDto getGroupInfo(String groupId);
}
