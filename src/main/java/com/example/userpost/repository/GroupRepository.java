package com.example.userpost.repository;

import com.example.userpost.model.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {

}
