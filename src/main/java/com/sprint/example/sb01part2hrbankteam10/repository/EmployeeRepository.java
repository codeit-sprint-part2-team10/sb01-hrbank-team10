package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
