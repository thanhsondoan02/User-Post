package com.example.userpost.model.user;

import com.example.userpost.model.base.BaseSqlEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseSqlEntity {

  @Column(name = "username", length = 50, nullable = false, unique = true)
  private String username;

  @Column(name = "email", length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 1)
  private Gender gender;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  public enum Gender {
    M, F
  }
}
