package com.example.userpost.service.impl;

import com.example.userpost.dto.request.group.CreateGroupRequestDto;
import com.example.userpost.dto.response.group.CreateGroupResponseDto;
import com.example.userpost.model.group.Group;
import com.example.userpost.model.group.GroupUser;
import com.example.userpost.model.user.User;
import com.example.userpost.repository.GroupRepository;
import com.example.userpost.repository.UserRepository;
import com.example.userpost.service.IGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GroupService implements IGroupService {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;

  public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
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
}
