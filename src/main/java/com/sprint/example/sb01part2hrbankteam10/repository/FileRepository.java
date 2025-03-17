package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import org.springframework.context.annotation.ReflectiveScan;
import org.springframework.data.jpa.repository.JpaRepository;

@ReflectiveScan
public interface FileRepository extends JpaRepository<File,Integer> {

}
