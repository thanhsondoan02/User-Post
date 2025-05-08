package com.example.userpost.repository;

import com.example.userpost.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {
  @Query("SELECT COUNT(g) > 0 FROM Group g WHERE g.id = :id AND g.state = 1")
  boolean existsAndActiveById(@Param("id") String id);

  @Query("SELECT COUNT(gu) > 0 FROM GroupUser gu WHERE gu.user.id = :userId AND gu.group.id = :groupId AND gu.role = 1 AND gu.state = 1")
  boolean isGroupAdmin(@Param("userId") String userId, @Param("groupId") String groupId);

  @Query("SELECT COUNT(gu) > 0 FROM GroupUser gu WHERE gu.user.id = :userId AND gu.group.id = :groupId AND gu.state = 1")
  boolean isInGroup(@Param("userId") String userId, @Param("groupId") String groupId);

  @Query("SELECT g FROM Group g WHERE g.id = :id AND g.state = 1")
  Optional<Group> findActiveById(@Param("id") String id);
}
