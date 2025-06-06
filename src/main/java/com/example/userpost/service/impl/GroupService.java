package com.example.userpost.service.impl;

import com.example.userpost.constant.State;
import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.GroupUserRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.GroupResponseDto;
import com.example.userpost.model.group.Group;
import com.example.userpost.model.group.GroupUser;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.GroupRepository;
import com.example.userpost.repository.GroupUserRepository;
import com.example.userpost.service.IGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService implements IGroupService {

  private final GroupRepository groupRepository;
  private final GroupUserRepository groupUserRepository;

  public GroupService(GroupRepository groupRepository, GroupUserRepository groupUserRepository) {
    this.groupRepository = groupRepository;
    this.groupUserRepository = groupUserRepository;
  }

  @Override
  public GroupResponseDto createGroup(CreateGroupRequestDto request) {
    Group group = new Group(request.getName(), request.getThumbnail());

    var groupUsers = new ArrayList<GroupUser>();
    for (var userReq : request.getUsers()) {
      var user = new User();
      user.setId(userReq.getUserId());
      groupUsers.add(new GroupUser(group, user, userReq.getRole()));
    }
    group.setGroupUsers(groupUsers);

    Group savedGroup = groupRepository.save(group);

    return new GroupResponseDto(savedGroup, true);
  }

  @Override
  public boolean isGroupExistAndActive(String id) {
    return groupRepository.existsAndActiveById(id);
  }

  @Override
  public boolean isGroupAdmin(String userId, String groupId) {
    return groupRepository.isGroupAdmin(userId, groupId);
  }

  @Override
  public boolean isInGroup(String userId, String groupId) {
    return groupRepository.isUserActiveInGroup(userId, groupId);
  }

  @Override
  public void updateGroup(String groupId, UpdateGroupRequestDto request) {
    Group group = groupRepository.findActiveById(groupId)
      .orElseThrow(() -> new RuntimeException("Group not found"));

    var name = request.getName();
    if (name != null) {
      group.setName(request.getName());
    }

    var thumbnail = request.getThumbnail();
    if (thumbnail != null) {
      group.setThumbnail(request.getThumbnail());
    }

    groupRepository.save(group);
  }

  @Override
  public void deleteGroup(String groupId) {
    Group group = groupRepository.findActiveById(groupId)
      .orElseThrow(() -> new RuntimeException("Group not found"));

    group.setState(State.INACTIVE);
    for (var groupUser : group.getGroupUsers()) {
      groupUser.setState(State.INACTIVE);
    }

    groupRepository.save(group);
  }

  @Override
  public void addUserToGroup(String groupId, GroupUserRequestDto request) {
    var userId = request.getUserId();
    var groupUser = groupRepository.findUserInGroupById(groupId, userId).orElse(null);
    if (groupUser == null) {
      // If this user hasn't been added to the group, insert a new record
      var newGroupUser = new GroupUser(groupId, userId, request.getRole());
      groupUserRepository.save(newGroupUser);
    } else {
      // If this user has been added to the group, check if they are inactive then update
      if (groupUser.getState() == State.INACTIVE) {
        groupUser.setState(State.ACTIVE);
        groupUser.setRole(request.getRole());
        groupUserRepository.save(groupUser);
      } else {
        throw new RuntimeException("User already in group");
      }
    }
  }

  @Override
  public void removeUserFromGroup(String groupId, String userId) {
    groupRepository.updateGroupUserState(groupId, userId, State.INACTIVE);
  }

  @Override
  public List<Group> getGroupList(String userId) {
    var groups = groupRepository.findActiveGroupsOfUser(userId);
    groups.forEach(v -> v.setGroupUsers(groupRepository.getActiveUsersInGroup(v.getId())));
    return groups;
  }

  @Override
  public List<Group> getAll() {
    return groupRepository.getActiveGroups();
  }

  @Override
  public GroupResponseDto getGroupInfo(String groupId) {
    var group = groupRepository.findActiveById(groupId)
      .orElseThrow(() -> new RuntimeException("Group not found"));
    group.setGroupUsers(groupRepository.getActiveUsersInGroup(groupId));
    return new GroupResponseDto(group, true);
  }
}
