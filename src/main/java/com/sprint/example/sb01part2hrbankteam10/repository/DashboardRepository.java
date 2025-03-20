package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<Employee, Integer> {

  /**
   * 직원 분포 조회
   */
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
      @Param("status") EmployeeStatus status);

  /**
   * 직원 추이 조회
   */

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

  /**
   * 직원수 조회
   */

  @Query("SELECT COUNT(e) FROM Employee e")
  int countEmployees();

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
  int countEmployeesByStatus(@Param("status") Employee.EmployeeStatus status);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status AND e.hireDate <= :toDate")
  int countEmployeesByStatusAndToDate(@Param("status") Employee.EmployeeStatus status,
      @Param("toDate") LocalDateTime toDate);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status AND e.hireDate >= :fromDate")
  int countEmployeesByStatusAndFromDate(@Param("status") Employee.EmployeeStatus status,
      @Param("fromDate") LocalDateTime fromDate);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.hireDate >= :fromDate AND e.hireDate <= :toDate")
  int countEmployeesByDateRange(@Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status AND e.hireDate >= :fromDate AND e.hireDate <= :toDate")
  int countEmployeesByAllConditions(
      @Param("status") Employee.EmployeeStatus status,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);

  @Query("SELECT COUNT(e) FROM Employee e WHERE e.updatedAt >= :fromDate AND e.updatedAt <= :toDate")
  int countEmployeesUpdatedInRange(@Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate);
}
