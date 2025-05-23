package com.example.userpost.model.user;

import com.example.userpost.constant.Gender;
import com.example.userpost.constant.Role;
import com.example.userpost.model.base.BaseSqlEntity;
import com.example.userpost.util.convert.RoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseSqlEntity {

  @NotNull
  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @NotNull
  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @NotNull
  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "gender", columnDefinition = "TINYINT")
  @Convert(converter = GenderConverter.class)
  private Gender gender;

  @Column(name = "role", columnDefinition = "TINYINT")
  @Convert(converter = RoleConverter.class)
  private Role role;

  @Column(name = "date_of_birth")
  private Long dateOfBirth;

  public User(String username, String email, String passwordHash, String fullName, Gender gender, Long dateOfBirth) {
    super();
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.fullName = fullName;
    this.gender = gender;
    this.dateOfBirth = dateOfBirth;
    this.role = Role.USER;
  }
}
