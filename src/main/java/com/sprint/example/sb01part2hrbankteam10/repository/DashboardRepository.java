package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Employee, Integer> {

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
  List<Object[]> findEmployeeDistribution(@Param("groupBy") String groupBy,
      @Param("status") String status);

  @Query(value =
      "WITH employee_counts AS (" +
          "  SELECT " +
          "    DATE_TRUNC(:unit, e.hire_date) AS grouped_date, " +
          "    COUNT(e.id) AS count " +
          "  FROM employees e " +
          "  WHERE e.hire_date BETWEEN :from AND :to " +
          "  GROUP BY grouped_date" +
          ")" +
          "SELECT " +
          "  TO_CHAR(grouped_date, 'YYYY-MM-DD') AS period, " +
          "  count, " +
          "  0 AS change, " +
          "  0.0 AS change_rate " +
          "FROM employee_counts " +
          "ORDER BY grouped_date",
      nativeQuery = true)
  List<Object[]> findEmployeeTrend(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("unit") String unit);
}
