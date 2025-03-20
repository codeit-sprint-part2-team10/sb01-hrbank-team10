package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>,
    JpaSpecificationExecutor<Employee> {

  boolean existsByEmail(String email);

  Optional<Employee> findTopByOrderByIdDesc();

  //직원수 조회 - 부서별
  Integer countByDepartmentId(Integer departmentId);

  //부서에 속한 직원이 있는지 확인
  boolean existsByDepartmentId(Integer id);

  Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

  @Query( value =
      "SELECT "
          + "new com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto("
          + "e.position , COUNT(e.id), COUNT(e) * 100.0 / (SELECT COUNT(e) FROM Employee e)) "
          + "FROM Employee e "
          + "WHERE e.status = :status "
          + "GROUP BY e.position"
  )
  List<EmployeeDistributionDto> findGroupByPosition(
      @Param("position") String position,
      @Param("status") EmployeeStatus status
  );

  @Query( value =
      "SELECT "
          + "new com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto("
          + "d.name, COUNT(e), COUNT(e) * 100.0 / (SELECT COUNT(e) FROM Employee e)) "
          + "FROM Employee e "
          + "LEFT JOIN Department d "
          + "ON e.department.id = d.id "
          + "WHERE e.status = :status "
          + "GROUP BY d.name"
  )
  List<EmployeeDistributionDto> findGroupByDepartment(
      @Param("position") String position,
      @Param("status") EmployeeStatus status
  );

  // 오늘 날짜 기준으로 (20일인 경우 20일을 기준으로)
//  @Query(value = """
//    SELECT new com.example.dto.EmployeeTrendDto(
//        DATE_TRUNC(:unit, e.hireDate),
//        COUNT(e),
//        COUNT(e) - LAG(COUNT(e), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hireDate)) AS change,
//        ((COUNT(e) - LAG(COUNT(e), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hireDate))) * 100.0 /
//         NULLIF(LAG(COUNT(e), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hireDate)), 0)) AS changeRate
//    )
//    FROM Employee e
//    WHERE e.hireDate BETWEEN :startDate AND :endDate
//    GROUP BY DATE_TRUNC(:unit, e.hireDate)
//    ORDER BY DATE_TRUNC(:unit, e.hireDate) ASC
//  """)
//  List<EmployeeTrendDto> findDataByUnit(
//      @Param("unit") String unit,
//      @Param("startDate") LocalDateTime startDate,
//      @Param("endDate") LocalDateTime endDate
//  );
  @Query(value = """
    SELECT 
        DATE_TRUNC(:unit, e.hire_date) AS date, 
        COUNT(e.id) AS count,
        COUNT(e.id) - LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date)) AS change,
        ((COUNT(e.id) - LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date))) * 100.0 / 
         NULLIF(LAG(COUNT(e.id), 1, 0) OVER (ORDER BY DATE_TRUNC(:unit, e.hire_date)), 0)) AS change_rate
    FROM Employee e
    WHERE e.hire_date BETWEEN :startDate AND :endDate
    GROUP BY DATE_TRUNC(:unit, e.hire_date)
    ORDER BY DATE_TRUNC(:unit, e.hire_date) ASC
""", nativeQuery = true)
  List<EmployeeTrendDto> findDataByUnit(
      @Param("unit") String unit,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );
}
