package com.example.userpost.repository;

import com.example.userpost.model.group.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, String> {

}
