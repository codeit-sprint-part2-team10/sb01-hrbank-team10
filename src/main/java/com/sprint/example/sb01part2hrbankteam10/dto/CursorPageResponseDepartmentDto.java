package com.sprint.example.sb01part2hrbankteam10.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponseDepartmentDto<T> {

  private List<T> data;
  private String nextCursor;
  private Long nextIdAfter;
  private int size;
  private long totalElements;
  private boolean hasNext;
}
