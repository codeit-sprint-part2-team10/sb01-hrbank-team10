package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import java.time.LocalDateTime;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Integer>, JpaSpecificationExecutor<EmployeeHistory>{
  LocalDateTime findLastModifiedAt();
}
