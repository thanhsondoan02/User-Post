package com.example.userpost.dto.response.group;

import com.example.userpost.model.group.GroupUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserResponseDto {
  private String id;
  private String userId;
  private String role;
  private String username;
  private String fullName;

  public GroupUserResponseDto(GroupUser groupUser, boolean includeUserDetails) {
    this.id = groupUser.getId();
    this.userId = groupUser.getUser().getId();
    this.role = groupUser.getRole().name();
    if (includeUserDetails) {
      this.username = groupUser.getUser().getUsername();
      this.fullName = groupUser.getUser().getFullName();
    }
  }
}
