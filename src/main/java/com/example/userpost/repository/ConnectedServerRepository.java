package com.example.userpost.repository;

import com.example.userpost.model.openid.ConnectedServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectedServerRepository extends JpaRepository<ConnectedServer, String> {

}
