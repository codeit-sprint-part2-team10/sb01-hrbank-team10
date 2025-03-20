package com.sprint.example.sb01part2hrbankteam10.controller.docs;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseChangeLogDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "Employee History", description = "직원 정보 수정 이력 관리 API")
public interface EmployeeHistoryDocs {

    @Operation(summary = "직원 정보 수정 이력 목록 조회", description = "직원 정보 수정 이력 목록을 조회합니다. 상세 변경 내용은 포함되지 않습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseChangeLogDto.class))
            )
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 정렬 필드",
                    content = @Content(schema = @Schema(implementation = ))
            )
    })
    @GetMapping
    ResponseEntity<CursorPageResponseChangeLogDto<ChangeLogDto>> getEmployeeHistories(
            @Parameter(description = "대상 직원 사번") @RequestParam(required = false) String employeeNumber,
            @Parameter(description = "이력 유형(CREATED, UPDATED, DELETED)") @RequestParam(required = false) String type,
            @Parameter(description = "내용") @RequestParam(required = false) String memo,
            @Parameter(description = "IP 주소") @RequestParam(required = false) String ipAddress,
            @Parameter(description = "수정 일시(부터)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atFrom,
            @Parameter(description = "수정 일시(까지)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atTo,
            @Parameter(description = "이전 페이지 마지막 요소 ID") @RequestParam(required = false) Integer idAfter,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 필드", example = "at") @RequestParam(defaultValue = "at") String sortField,
            @Parameter(description = "정렬 방향", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection
    );

    @Operation(summary = "직원 정보 수정 이력 상세 조회", description = "직원 정보 수정 이력의 상세 정보를 조회합니다. 변경 상세 내용이 포함됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })

}
