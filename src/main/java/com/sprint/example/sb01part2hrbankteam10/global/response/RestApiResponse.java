package com.sprint.example.sb01part2hrbankteam10.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sprint.example.sb01part2hrbankteam10.global.exception.ErrorResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
@JsonInclude(value = Include.NON_NULL)
@Builder(access = AccessLevel.PRIVATE)
public class RestApiResponse<T> {

  private boolean success;
  private String message;
  private HttpStatus status;
  private T content;
  private ErrorResponse error;

  public static <T> RestApiResponse<T> success(HttpStatus status, T data) {
    return RestApiResponse.<T>builder()
        .content(data)
        .build();
  }

  public static RestApiResponse<Void> success(HttpStatus status, String message) {
    return RestApiResponse.<Void>builder()
        .success(true)
        .status(status)
        .message(message)
        .build();
  }

  public static <T> RestApiResponse<T> failure(ErrorResponse errorResponse) {
    return RestApiResponse.<T>builder()
        .success(false)
        .status(errorResponse.getStatus())
        .error(errorResponse)
        .build();
  }
}
