package com.sprint.example.sb01part2hrbankteam10.repository.specification;

import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

  // 포함 조건
  public static Specification<Employee> likeName(String name) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("name"), '%'+ name + '%');
  }

  public static Specification<Employee> likeEmail(String email) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("email"), '%'+ email + '%');
  }

  public static Specification<Employee> likeEmployeeNumber(String employeeNumber) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("employeeNumber"), '%'+ employeeNumber + '%');
  }

  public static Specification<Employee> likeDepartmentName(String departmentName) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("departmentName"), '%'+ departmentName + '%');
  }

  public static Specification<Employee> likePosition(String position) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.like(root.get("position"), '%'+ position + '%');
  }

  // 일치 조건
  public static Specification<Employee> equalStatus(EmployeeStatus status) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.equal(root.get("status"), status);
  }

  // 범위 조건
  public static Specification<Employee> equalOrGreaterHireDateFrom(LocalDateTime hireDateFrom) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.greaterThanOrEqualTo(root.get("hireDate"), hireDateFrom);
  }

  public static Specification<Employee> equalOrLessHireDateTo(LocalDateTime hireDateTo) {
    return (root, query, criteriaBuilder)
        -> criteriaBuilder.lessThanOrEqualTo(root.get("hireDate"), hireDateTo);
  }

}
