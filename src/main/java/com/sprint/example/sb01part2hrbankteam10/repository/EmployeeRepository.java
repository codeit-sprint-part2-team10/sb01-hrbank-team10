package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  boolean existsByEmail(String email);

  Optional<Employee> findTopByOrderByIdDesc();

  //직원수 조회 - 부서별
  Integer countByDepartmentId(Integer departmentId);

  //부서에 속한 직원이 있는지 확인
  boolean existsByDepartmentId(Integer id);

  @Query(value =
      "SELECT " +
          "  name AS group_key, " +
          "  SUM(count) AS count, " +
          "  ROUND(SUM(count) * 100.0 / total_count, 2) AS percentage " +
          "FROM (" +
          "  SELECT " +
          "    CASE WHEN :groupBy = 'departments' THEN COALESCE(d.name, '미지정') " +
          "         ELSE COALESCE(e.position, '미지정') END AS name, " +
          "    COUNT(e.id) AS count, " +
          "    COALESCE(d.name, '미지정') AS dept_name, " +
          "    COALESCE(e.position, '미지정') AS pos_name " +
          "  FROM employees e " +
          "  LEFT JOIN departments d ON e.department_id = d.id " +
          "  WHERE e.status = :status " +
          "  GROUP BY COALESCE(d.name, '미지정'), COALESCE(e.position, '미지정')" +
          ") result, " +
          "(SELECT COUNT(*) AS total_count FROM employees WHERE status = :status) total " +
          "GROUP BY name, total_count",
      nativeQuery = true)
  List<Object[]> findEmployeeDistribution(@Param("groupBy") String groupBy, @Param("status") String status);
}