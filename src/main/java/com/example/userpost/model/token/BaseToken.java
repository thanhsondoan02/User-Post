package com.example.userpost.model.token;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseToken extends BaseSqlEntity {
  @NotNull
  @Column(name = "refresh_token", nullable = false)
  protected String refreshToken;

  @Column(name = "refresh_expired_at", nullable = false)
  protected Long refreshExpiredAt;

  @NotNull
  @Column(name = "access_token", nullable = false)
  protected String accessToken;

  @Column(name = "access_expired_at", nullable = false)
  protected Long accessExpiredAt;

  protected BaseToken() {
    super();
  }

  protected BaseToken(String refreshToken, Long refreshExpiredAt, String accessToken, Long accessExpiredAt) {
    super();
    this.refreshToken = refreshToken;
    this.refreshExpiredAt = refreshExpiredAt;
    this.accessToken = accessToken;
    this.accessExpiredAt = accessExpiredAt;
  }
}
