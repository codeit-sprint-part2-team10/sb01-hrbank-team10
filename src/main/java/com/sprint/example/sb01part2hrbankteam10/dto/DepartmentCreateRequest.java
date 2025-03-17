package com.sprint.example.sb01part2hrbankteam10.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import javax.xml.stream.XMLInputFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentCreateRequest {

  @NotNull
  @Size(min = 1, max = 50)
  private String name;

  @NotNull
  @Size(min = 1, max = 255)
  private String description;

  @NotNull
  private LocalDateTime establishedDate;

}
