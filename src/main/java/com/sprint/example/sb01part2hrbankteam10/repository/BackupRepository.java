package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Integer> {

  // 특정 상태의 가장 최근 백업 가져오기
  Backup findFirstByStatusOrderByStartedAtDesc(@Param("status") Backup.BackupStatus status);

  // 백업 목록 조회 (필터링 및 정렬 포함)
  @Query("SELECT new com.sprint.example.sb01part2hrbankteam10.dto.BackupDto(b.id, b.workerIpAddress, b.startedAt, b.endedAt, b.status, b.file.id) " +
          "FROM Backup b " +
          "WHERE (:worker IS NULL OR b.workerIpAddress = :worker) " +
          "AND (:status IS NULL OR b.status = :status) " +
          "AND (:startedAtFrom IS NULL OR b.startedAt >= :startedAtFrom) " +
          "AND (:startedAtTo IS NULL OR b.startedAt <= :startedAtTo) " +
          "AND (:idAfter IS NULL OR b.id > :idAfter) " +
          "ORDER BY " +
          "   CASE WHEN :sortField = 'startedAt' AND :sortDirection = 'ASC' THEN b.startedAt END ASC, " +
          "   CASE WHEN :sortField = 'startedAt' AND :sortDirection = 'DESC' THEN b.startedAt END DESC")
  Page<BackupDto> findBackups(
          @Param("worker") String worker,
          @Param("status") Backup.BackupStatus status,
          @Param("startedAtFrom") LocalDateTime startedAtFrom,
          @Param("startedAtTo") LocalDateTime startedAtTo,
          @Param("idAfter") Integer idAfter,
          Pageable pageable);


  // 가장 최근 완료된 백업 시간 조회
  @Query("SELECT MAX(b.endedAt) FROM Backup b WHERE b.status = 'COMPLETED'")
  LocalDateTime findLastCompletedBackupAt();

  @Query("SELECT b FROM Backup b WHERE b.status = 'COMPLETED' ORDER BY b.createdAt DESC")
  Optional<Backup> findLastCompletedBackup();

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.updatedAt <= :backupDate")
  int countEmployeesBackedUpAt(@Param("backupDate") LocalDateTime backupDate);
}