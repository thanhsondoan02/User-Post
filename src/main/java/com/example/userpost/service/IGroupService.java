package com.example.userpost.service;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.GroupUserRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.GroupResponseDto;
import com.example.userpost.model.group.Group;

import java.util.List;

public interface IGroupService {
  GroupResponseDto createGroup(CreateGroupRequestDto request);

  boolean isGroupExistAndActive(String id);

  boolean isInGroup(String userId, String groupId);

  boolean isGroupAdmin(String userId, String groupId);

  void updateGroup(String groupId, UpdateGroupRequestDto request);

  void deleteGroup(String groupId);

  void addUserToGroup(String groupId, GroupUserRequestDto request);

  void removeUserFromGroup(String groupId, String userId);

  List<Group> getGroupList(String userId);

  List<Group> getAll();

  GroupResponseDto getGroupInfo(String groupId);
}
