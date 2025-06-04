package com.example.userpost.model.openid;

import com.example.userpost.constant.ServerOwner;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.util.convert.ServerOwnerConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servers")
@Getter
@Setter
public class Server extends BaseSqlEntity {

  @Column(name = "domain", nullable = false, unique = true)
  protected String domain;

  @Column(name = "owner", nullable = false, columnDefinition = "TINYINT")
  @Convert(converter = ServerOwnerConverter.class)
  protected ServerOwner owner;

  @OneToMany(mappedBy = "targetServer", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Connection> connections = new ArrayList<>();

  public Server() {
    super();
    this.owner = ServerOwner.EXTERNAL;
  }

  public Server(String domain) {
    super();
    this.domain = domain;
    this.owner = ServerOwner.EXTERNAL;
  }
}
