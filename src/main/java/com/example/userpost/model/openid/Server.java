package com.example.userpost.model.openid;

import com.example.userpost.constant.ServerOwner;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.util.convert.ServerOwnerConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "servers")
@Getter
@Setter
public class Server extends BaseSqlEntity {

  @Column(name = "name", length = 100, nullable = false)
  protected String name;

  @Column(name = "domain", nullable = false, unique = true)
  protected String domain;

  @Column(name = "owner", nullable = false, columnDefinition = "TINYINT")
  @Convert(converter = ServerOwnerConverter.class)
  protected ServerOwner owner;

  public Server() {
    super();
    this.owner = ServerOwner.EXTERNAL;
  }

  public Server(String name, String domain) {
    super();
    this.name = name;
    this.domain = domain;
    this.owner = ServerOwner.EXTERNAL;
  }
}
