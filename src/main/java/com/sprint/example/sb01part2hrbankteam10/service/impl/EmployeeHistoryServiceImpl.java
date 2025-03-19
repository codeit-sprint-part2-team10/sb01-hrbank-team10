package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.*;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeHistoryErrorCode;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.GlobalErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.EmployeeHistoryMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryServiceImpl implements EmployeeHistoryService {

    private final EmployeeHistoryRepository employeeHistoryRepository;

    @Transactional
    @Override
    public ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
                               String memo, EmployeeCreateRequest changes, EmployeeDto afterData, String clientIp) {

        List<DiffDto> diffs = compareChanges(null, afterData);

        Map<String, Object> changedFields = diffs.stream()
                .collect(Collectors.toMap(DiffDto::getPropertyName, EmployeeHistoryMapper::toDetailMap));

        EmployeeHistory history = EmployeeHistory.builder()
                .employeeNumber(employeeNumber)
                .type(type)
                .memo(memo)
                .modifiedAt(LocalDateTime.now())
                .ipAddress(clientIp)
                .changedFields(changedFields) // 변경 사항 저장
                .build();

//        EmployeeHistory history = EmployeeHistoryMapper.toEntity(employeeNumber, type, memo, changes, afterData, clientIp);


        EmployeeHistory savedHistory = employeeHistoryRepository.save(history);

        return EmployeeHistoryMapper.toChangeLogDto(savedHistory);
    }

    public List<DiffDto> compareChanges(EmployeeDto beforeData, EmployeeDto afterData) {
        List<DiffDto> changes = new ArrayList<>();
        if (beforeData == null || afterData == null) {
            return changes;
        }

        addDiffIfChanged(changes, "name", beforeData.getName(), afterData.getName());
        addDiffIfChanged(changes, "email", beforeData.getEmail(), afterData.getEmail());
        addDiffIfChanged(changes, "직함", beforeData.getPosition(), afterData.getPosition());
        addDiffIfChanged(changes, "departmentId", beforeData.getDepartmentId(), afterData.getDepartmentId());
        addDiffIfChanged(changes, "departmentName", beforeData.getDepartmentName(), afterData.getDepartmentName());
        addDiffIfChanged(changes, "hireDate", beforeData.getHireDate(), afterData.getHireDate());
        addDiffIfChanged(changes, "status", beforeData.getStatus(), afterData.getStatus());
        // 프로필 이미지 비교는 주석 처리되어 있으므로 그대로 유지합니다.
//        if (!Objects.equals(beforeData.getProfileImageId(), afterData.getProfileImageId())) {
//            changes.add(new DiffDto("profileImageId", String.valueOf(beforeData.getProfileImageId()), String.valueOf(afterData.getProfileImageId())));
//        }

        return changes;
    }

    private static void addDiffIfChanged(List<DiffDto> changes, String propertyName, Object beforeValue, Object afterValue) {
        String beforeStr = beforeValue != null ? beforeValue.toString() : null;
        String afterStr = afterValue != null ? afterValue.toString() : null;
        if (!Objects.equals(beforeStr, afterStr)) {
            changes.add(new DiffDto(propertyName, beforeStr, afterStr));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponseChangeLogDto getEmployeeHistoriesByCursor(
            String employeeNumber,
            String type,
            String memo,
            String ipAddress,
            LocalDateTime atFrom,
            LocalDateTime atTo,
            Integer idAfter,
            int size,
            String sortField,
            String sortDirection
    ) {
        if (!"at".equalsIgnoreCase(sortField) && !"ipAddress".equalsIgnoreCase(sortField)) {
            throw new RestApiException(
                    EmployeeHistoryErrorCode.INVALID_SORT_FIELD,
                    "잘못된 요청 또는 지원하지 않는 정렬 필드입니다."
            );
        }

        // 1. 동적 조건 생성
        Specification<EmployeeHistory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (employeeNumber != null && !employeeNumber.isEmpty()) {
                predicates.add(cb.like(root.get("employeeNumber"), "%" + employeeNumber + "%"));
            }
            if (type != null && !type.isEmpty()) {
                try {
                    EmployeeHistory.ChangeType changeType = EmployeeHistory.ChangeType.valueOf(type);
                    predicates.add(cb.equal(root.get("type"), changeType));
                } catch (IllegalArgumentException e) {
                    // 잘못된 유형이면 조건 무시 또는 예외 처리
                }
            }
            if (memo != null && !memo.isEmpty()) {
                predicates.add(cb.like(root.get("memo"), "%" + memo + "%"));
            }
            if (ipAddress != null && !ipAddress.isEmpty()) {
                predicates.add(cb.like(root.get("ipAddress"), "%" + ipAddress + "%"));
            }
            if (atFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("modifiedAt"), atFrom));
            }
            if (atTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("modifiedAt"), atTo));
            }
            if (idAfter != null) {
                if ("desc".equalsIgnoreCase(sortDirection)) {
                    predicates.add(cb.lessThan(root.get("id"), idAfter));
                } else {
                    predicates.add(cb.greaterThan(root.get("id"), idAfter));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 2. 정렬 및 페이지네이션 설정
        String entitySortField = sortField.equals("at") ? "modifiedAt" : sortField;
        Sort sort = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.by(entitySortField).ascending()
                : Sort.by(entitySortField).descending();
        Pageable pageable = PageRequest.of(0, size + 1, sort);

        Page<EmployeeHistory> pageResult = employeeHistoryRepository.findAll(spec, pageable);
        List<EmployeeHistory> histories = pageResult.getContent();

        // 3. 다음 페이지 존재 여부 판단
        boolean hasNext = histories.size() > size;
        if (hasNext) {
            histories = histories.subList(0, size);
        }

        // 4. ChangeLogDto로 변환 (Mapper 메서드를 사용)
        List<ChangeLogDto> content = histories.stream()
                .map(EmployeeHistoryMapper::toChangeLogDto)
                .collect(Collectors.toList());

        // 5. 다음 페이지 커서 계산
        Integer nextIdAfter = null;
        String nextCursor = null;
        if (hasNext) {
            EmployeeHistory nextHistory = pageResult.getContent().get(size);
            nextIdAfter = nextHistory.getId();
            nextCursor = Base64.getEncoder().encodeToString(("{\"id\":" + nextIdAfter + "}").getBytes());
        }

        // 6. 전체 건수 조회
        long totalElements = employeeHistoryRepository.count(spec);

        // 7. 응답 DTO 구성
        CursorPageResponseChangeLogDto<ChangeLogDto> response =
                new CursorPageResponseChangeLogDto<>(content, nextCursor, nextIdAfter, size, totalElements, hasNext);

        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public List<DiffDto> getChangeDiffs(Integer id) {
        EmployeeHistory history = employeeHistoryRepository.findById(id)
                .orElseThrow(() -> new RestApiException(
                        EmployeeHistoryErrorCode.EMPLOYEE_HISTORY_NOT_FOUND,
                        "해당 ID를 가진 직원이 없습니다."));
        return EmployeeHistoryMapper.toDiffList(history);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countEmployeeHistories(String employeeNumber,
                                       String type,
                                       String memo,
                                       String ipAddress,
                                       LocalDateTime atFrom,
                                       LocalDateTime atTo,
                                       Integer idAfter,
                                       int size,
                                       String sortField,
                                       String sortDirection) {

        if(atFrom != null && atTo != null && atFrom.isAfter(atTo)) {
            throw new RestApiException(
                    EmployeeHistoryErrorCode.INVALID_DATE_RANGE,
                    "유효하지 않은 날짜 범위입니다. 시작 일시가 종료 일시보다 이후일 수 없습니다."
            );
        }

        final LocalDateTime finalAtFrom;
        final LocalDateTime finalAtTo;

        if(atFrom == null || atTo == null) {
            finalAtTo = LocalDateTime.now();
            finalAtFrom = finalAtTo.minusDays(7);
        } else {
            finalAtFrom = atFrom;
            finalAtTo = atTo;
        }

        Specification<EmployeeHistory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(employeeNumber != null && !employeeNumber.isEmpty()) {
                predicates.add(cb.like(root.get("employeeNumber"), "%" + employeeNumber + "%"));
            }
            if(type != null && !type.isEmpty()) {
                try{
                    EmployeeHistory.ChangeType ct = EmployeeHistory.ChangeType.valueOf(type);
                    predicates.add(cb.equal(root.get("type"), ct));
                } catch (IllegalArgumentException e) {

                }
            }
            if(memo != null && !memo.isEmpty()) {
                predicates.add(cb.like(root.get("memo"), "%" + memo + "%"));
            }
            if(ipAddress != null && !ipAddress.isEmpty()) {
                predicates.add(cb.like(root.get("ipAddress"), "%" + ipAddress + "%"));
            }
            if(finalAtFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("modifiedAt"), finalAtFrom));
            }
            if(finalAtTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("modifiedAt"), finalAtTo));
            }
            if(idAfter != null) {
                if("desc".equalsIgnoreCase(sortDirection)) {
                    predicates.add(cb.lessThan(root.get("id"), idAfter));
                } else {
                    predicates.add(cb.greaterThan(root.get("id"), idAfter));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return employeeHistoryRepository.count(spec);
    }
}
