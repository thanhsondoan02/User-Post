package com.example.userpost.model.user;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @Column(length = 32, nullable = false)
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(length = 1)
  private Gender gender;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public enum Gender {
    M, F
  }
}
