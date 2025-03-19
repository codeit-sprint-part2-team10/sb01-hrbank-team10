package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Integer>{
//  LocalDateTime findLastCompletedBackup();

  @Query("SELECT b FROM Backup b WHERE b.status = 'COMPLETED' ORDER BY b.createdAt DESC")
  Optional<Backup> findLastCompletedBackup();

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.updatedAt <= :backupDate")
  int countEmployeesBackedUpAt(@Param("backupDate") LocalDateTime backupDate);
}
