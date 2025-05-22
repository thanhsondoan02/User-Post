package com.example.userpost.model.openid;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseConnection extends BaseSqlEntity {

  @Column(name = "name", length = 100, nullable = false)
  protected String name;

  @Column(name = "domain", nullable = false)
  protected String domain;

  @Column(name = "callback_url", nullable = false)
  protected String callbackUrl;

  public BaseConnection(String name, String domain, String callbackUrl) {
    super();
    this.name = name;
    this.domain = domain;
    this.callbackUrl = callbackUrl;
  }
}
