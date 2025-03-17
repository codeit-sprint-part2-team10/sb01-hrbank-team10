package com.sprint.example.sb01part2hrbankteam10.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  @Column(name = "content_type", length = 100, nullable = false)
  private String contentType;

  @Column(name = "size", nullable = false)
  private BigInteger size;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition= "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public File(String name, String contentType, BigInteger size) {
    this.name = name;
    this.contentType = contentType;
    this.size = size;
  }
}
