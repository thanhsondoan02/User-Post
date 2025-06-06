package com.example.userpost.model.group;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`groups`")
@Getter
@Setter
@NoArgsConstructor
public class Group extends BaseSqlEntity {

  @NotNull
  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "thumbnail", length = 100)
  private String thumbnail;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupUser> groupUsers = new ArrayList<>();

  public Group(String name, String thumbnail) {
    super();
    this.name = name;
    this.thumbnail = thumbnail;
  }
}
