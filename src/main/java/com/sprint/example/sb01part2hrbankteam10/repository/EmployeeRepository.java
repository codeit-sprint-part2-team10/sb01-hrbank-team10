package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  boolean existsByEmail(String email);

  Optional<Employee> findTopByOrderByIdDesc();

  //직원수 조회 - 부서별
  Integer countByDepartmentId(Integer departmentId);

  //부서에 속한 직원이 있는지 확인
  boolean existsByDepartmentId(Integer id);
}
