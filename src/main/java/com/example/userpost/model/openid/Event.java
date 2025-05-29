package com.example.userpost.model.openid;

import com.example.userpost.constant.HookEvent;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.model.group.GroupUser;
import com.example.userpost.util.convert.HookEventConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event extends BaseSqlEntity {

  @Column(name = "code", columnDefinition = "TINYINT", nullable = false, unique = true)
  @Convert(converter = HookEventConverter.class)
  private HookEvent type;

  @Column(name = "name", length = 20, nullable = false)
  private String name;

  @Column(name = "description", length = 100)
  private String description;

  public Event(Integer type, String name, String description) {
    super();
    this.type = HookEvent.fromCode(type);
    this.name = name;
    this.description = description;
  }
}
