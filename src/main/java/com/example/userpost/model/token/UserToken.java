package com.example.userpost.model.token;

import com.example.userpost.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_tokens")
@Getter
@Setter
public class UserToken extends BaseToken {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public UserToken() {
    super();
  }

  public UserToken(User user, String refreshToken, Long refreshExpiredAt, String accessToken, Long accessExpiredAt) {
    super(refreshToken, refreshExpiredAt, accessToken, accessExpiredAt);
    this.user = user;
  }
}
