package com.example.userpost.repository;

import com.example.userpost.constant.State;
import com.example.userpost.model.openid.AcceptedConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcceptedConnectionRepository extends JpaRepository<AcceptedConnection, String> {

  List<AcceptedConnection> findAllByState(State state);
}
