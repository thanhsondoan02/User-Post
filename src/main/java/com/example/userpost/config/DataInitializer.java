package com.example.userpost.config;

import com.example.userpost.constant.ServerOwner;
import com.example.userpost.model.openid.Server;
import com.example.userpost.repository.ServerRepository;
import com.example.userpost.util.LogUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

  private final ServerRepository serverRepository;
  private final ServerConfig serverConfig;

  public DataInitializer(ServerRepository serverRepository, ServerConfig serverConfig) {
    this.serverRepository = serverRepository;
    this.serverConfig = serverConfig;
  }

  @PostConstruct
  public void initOwnServer() {
    if (!serverRepository.existsOwnerServer()) {
      var server = new Server(serverConfig.getName(), serverConfig.getDomain());
      server.setOwner(ServerOwner.OWN);
      serverRepository.save(server);
      LogUtils.log(getClass(), "Owner server created with domain: " + serverConfig.getDomain());
    } else {
      LogUtils.log(getClass(), "Owner server already exists. No need to re-initialize.");
    }
  }
}
