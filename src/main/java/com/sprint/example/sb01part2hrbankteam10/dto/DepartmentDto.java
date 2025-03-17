package com.sprint.example.sb01part2hrbankteam10.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {

  @NotNull
  private Integer id;

  @NotNull
  private String name;

  @NotNull
  private String description;

  @NotNull
  private LocalDateTime establishedDate;

  private Integer employeeCount;

}
