package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File,Integer> {

}
