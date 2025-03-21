package com.sprint.example.sb01part2hrbankteam10.repository;

import com.sprint.example.sb01part2hrbankteam10.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent,Integer> {
  Integer findByName(String name);
}
