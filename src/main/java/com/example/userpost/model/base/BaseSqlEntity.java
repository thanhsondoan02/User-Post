package com.example.userpost.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
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

  @PrePersist
  public void generateId() {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString().replace("-", "");
    }
  }
}
