package com.example.userpost.model.base;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class BaseSqlEntity implements Serializable {

  @Id
  @Column(name = "id", length = 32, nullable = false)
  protected String id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  protected Long createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  protected Long updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  protected State state = State.ACTIVE;

  public enum State {
    ACTIVE,
    INACTIVE
  }
}
