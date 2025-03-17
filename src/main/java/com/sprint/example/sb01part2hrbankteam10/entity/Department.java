package com.sprint.example.sb01part2hrbankteam10.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "departments")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "name", length = 50, nullable = false, unique = true)
  private String name;

  @Column(name = "description", length = 255, nullable = false)
  private String description;

  @Column(name = "established_date", nullable = false)
  private LocalDateTime establishedDate;

  @Builder
  public Department(String name, String description, LocalDateTime establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }
}
