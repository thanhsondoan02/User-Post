package com.example.userpost.dto.response.group;

import com.example.userpost.model.group.GroupUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupUserResponseDto {
  private String userId;
  private String role;
  private String username;
  private String fullName;

  public GroupUserResponseDto(GroupUser groupUser) {
    this.userId = groupUser.getUser().getId();
    this.role = groupUser.getRole().name();
    this.username = groupUser.getUser().getUsername();
    this.fullName = groupUser.getUser().getFullName();
  }
}
