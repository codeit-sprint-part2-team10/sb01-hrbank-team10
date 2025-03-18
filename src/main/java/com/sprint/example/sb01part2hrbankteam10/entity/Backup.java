package com.sprint.example.sb01part2hrbankteam10.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @CreatedDate
  @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  @JoinColumn(name = "file_id")
  private File file;

  @Column(name = "started_at", columnDefinition = "timestamp with time zone", nullable = false)
  private LocalDateTime startedAt;

  @Column(name = "ended_at", columnDefinition = "timestamp with time zone")
  private LocalDateTime endedAt;

  @Column(name = "worker_ip_address", length = 255, nullable = false)
  private String workerIpAddress;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BackupStatus status;

  @Column(name = "batch_done_at", columnDefinition = "timestamp with time zone")
  private LocalDateTime batchDoneAt;

  @Builder
  public Backup(File file, LocalDateTime startedAt, LocalDateTime endedAt, String workerIpAddress,
      BackupStatus status, LocalDateTime batchDoneAt) {
    this.file = file;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.workerIpAddress = workerIpAddress;
    this.status = status;
    this.batchDoneAt = batchDoneAt;
  }

  public enum BackupStatus {
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    FAILED
  }
}

