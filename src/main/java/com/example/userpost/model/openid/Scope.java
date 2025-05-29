package com.example.userpost.model.openid;

import com.example.userpost.constant.HookScope;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.util.convert.HookScopeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scopes")
@Getter
@Setter
@NoArgsConstructor
public class Scope extends BaseSqlEntity {

  @Column(name = "code", columnDefinition = "TINYINT", nullable = false, unique = true)
  @Convert(converter = HookScopeConverter.class)
  private HookScope type;

  @Column(name = "name", length = 20, nullable = false)
  private String name;

  @Column(name = "description", length = 100, nullable = false)
  private String description;

  @OneToMany(mappedBy = "scope", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventScope> events = new ArrayList<>();

  public Scope(Integer type, String name, String description) {
    super();
    this.type = HookScope.fromCode(type);
    this.name = name;
    this.description = description;
  }
}
