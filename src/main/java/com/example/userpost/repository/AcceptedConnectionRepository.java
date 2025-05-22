package com.example.userpost.repository;

import com.example.userpost.model.openid.AcceptedConnection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedConnectionRepository extends JpaRepository<AcceptedConnection, String> {

}
