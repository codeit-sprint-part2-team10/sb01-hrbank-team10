package com.sprint.example.sb01part2hrbankteam10.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "employee_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "employee_number", nullable = false, length = 50)
  private String employeeNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ChangeType type;

  @Column(name = "memo", columnDefinition = "TEXT")
  private String memo;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "ip_address", nullable = false, length = 50)
  private String ipAddress;

  @Column(name = "changed_fields", columnDefinition = "jsonb", nullable = false)
  private String changedFields;

  @CreationTimestamp
  @Column(name = "logged_at", columnDefinition= "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime loggedAt;

  @Column(name = "changed_by", nullable = false, length = 50)
  private String changedBy;

  @Builder
  public EmployeeHistory(String employeeNumber, ChangeType type, String memo,
      LocalDateTime modifiedAt, String ipAddress, String changedFields, String changedBy) {
    this.employeeNumber = employeeNumber;
    this.type = type;
    this.memo = memo;
    this.modifiedAt = modifiedAt;
    this.ipAddress = ipAddress;
    this.changedFields = changedFields;
    this.changedBy = changedBy;
  }

  public enum ChangeType {
    CREATED, UPDATED, DELETED
  }
}
