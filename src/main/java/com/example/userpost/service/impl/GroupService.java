package com.example.userpost.service.impl;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.request.group.UpdateGroupRequestDto;
import com.example.userpost.dto.response.group.CreateGroupResponseDto;
import com.example.userpost.model.group.Group;
import com.example.userpost.model.group.GroupUser;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.GroupRepository;
import com.example.userpost.service.IGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GroupService implements IGroupService {

  private final GroupRepository groupRepository;

  public GroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  @Override
  public CreateGroupResponseDto createGroup(CreateGroupRequestDto request) {
    Group group = new Group(request.getName(), request.getThumbnail());

    var groupUsers = new ArrayList<GroupUser>();
    for (var userReq : request.getUsers()) {
      var user = new User();
      user.setId(userReq.getUserId());
      groupUsers.add(new GroupUser(group, user, userReq.getRole()));
    }
    group.setGroupUsers(groupUsers);

    Group savedGroup = groupRepository.save(group);

    return new CreateGroupResponseDto(
      savedGroup.getId(),
      savedGroup.getName(),
      savedGroup.getThumbnail(),
      savedGroup.getGroupUsers()
    );
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
    return groupRepository.isInGroup(userId, groupId);
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
}
