package com.example.userpost.model.group;

import com.example.userpost.constant.GroupRole;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "group_users",
  indexes = {
    @Index(name = "idx_group_users_group_id", columnList = "group_id"),
    @Index(name = "idx_group_users_user_id", columnList = "user_id")
  },
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "group_id"})
  }
)
@Getter
@Setter
@NoArgsConstructor
public class GroupUser extends BaseSqlEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "role", nullable = false, columnDefinition = "TINYINT")
  @Convert(converter = GroupRoleConverter.class)
  protected GroupRole role;

  public GroupUser(String groupId, String userId, GroupRole role) {
    super();
    this.group = new Group();
    this.group.setId(groupId);
    this.user = new User();
    this.user.setId(userId);
    this.role = role;
  }

  public GroupUser(Group group, User user, GroupRole role) {
    super();
    this.group = group;
    this.user = user;
    this.role = role;
  }
}
