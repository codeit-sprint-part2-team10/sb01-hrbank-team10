package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DiffDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class EmployeeHistoryServiceImpl implements EmployeeHistoryService {

    private final EmployeeHistoryRepository employeeHistoryRepository;

    @Transactional
    @Override
    public ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
                       String memo, EmployeeDto beforeData,
                       EmployeeDto afterData,
                       HttpServletRequest request) {
        List<DiffDto> changedFields = compareChanges(beforeData, afterData);

        String clientIp = getClientIp(request);

        Map<String, Object> changedFieldMap = changedFields.stream()
                .collect(Collectors.toMap(
                        diff -> diff.getPropertyName().toString(),
                        diff -> {
                            Map<String, Object> detailMap = new HashMap<>();
                            detailMap.put("before", diff.getBefore());
                            detailMap.put("after", diff.getAfter());
                            return detailMap;
                        }
                ));

        EmployeeHistory history = EmployeeHistory.builder()
                .employeeNumber(employeeNumber)
                .type(type)
                .memo(memo)
                .modifiedAt(LocalDateTime.now())
                .ipAddress(clientIp)
                .changedFields(changedFieldMap)
                .build();

        EmployeeHistory savedHistory = employeeHistoryRepository.save(history);

        return ChangeLogDto.fromEntity(savedHistory);
    }

    private List<DiffDto> compareChanges(EmployeeDto beforeData, EmployeeDto afterData) {
        List<DiffDto> changes = new ArrayList<>();

        if(!Objects.equals(beforeData.getName(), afterData.getName())) {
            changes.add(new DiffDto("name", beforeData.getName(), afterData.getName()));
        }
        if(!Objects.equals(beforeData.getEmail(), afterData.getEmail())) {
            changes.add(new DiffDto("email", beforeData.getEmail(), afterData.getEmail()));
        }
        if (!Objects.equals(beforeData.getPosition(), afterData.getPosition())) {
            changes.add(new DiffDto("position", beforeData.getPosition(), afterData.getPosition()));
        }
        if (!Objects.equals(beforeData.getDepartmentId(), afterData.getDepartmentId())) {
            changes.add(new DiffDto("departmentId", String.valueOf(beforeData.getDepartmentId()), String.valueOf(afterData.getDepartmentId())));
        }
        if (!Objects.equals(beforeData.getDepartmentName(), afterData.getDepartmentName())) {
            changes.add(new DiffDto("departmentName", beforeData.getDepartmentName(), afterData.getDepartmentName()));
        }
        if (!Objects.equals(beforeData.getHireDate(), afterData.getHireDate())) {
            changes.add(new DiffDto("hireDate", beforeData.getHireDate().toString(), afterData.getHireDate().toString()));
        }
        if (!Objects.equals(beforeData.getStatus(), afterData.getStatus())) {
            changes.add(new DiffDto("status", beforeData.getStatus().toString(), afterData.getStatus().toString()));
        }
        if (!Objects.equals(beforeData.getProfileImageId(), afterData.getProfileImageId())) {
            changes.add(new DiffDto("profileImageId", String.valueOf(beforeData.getProfileImageId()), String.valueOf(afterData.getProfileImageId())));
        }

        return changes;
    }


    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
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
                // 커서가 있을 경우, 정렬 방향에 따라 id 조건 적용
                if ("desc".equalsIgnoreCase(sortDirection)) {
                    // 내림차순: id가 작아야 함
                    predicates.add(cb.lessThan(root.get("id"), idAfter));
                } else {
                    // 오름차순: id가 커야 함
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
        // size보다 1건 더 조회해서 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1, sort);

        Page<EmployeeHistory> pageResult = employeeHistoryRepository.findAll(spec, pageable);
        List<EmployeeHistory> histories = pageResult.getContent();

        // 3. 다음 페이지 존재 여부 판단
        boolean hasNext = histories.size() > size;
        if (hasNext) {
            histories = histories.subList(0, size);
        }

        // 4. ChangeLogDto로 변환
        List<ChangeLogDto> content = histories.stream()
                .map(history -> new ChangeLogDto(
                        history.getId(),
                        history.getEmployeeNumber(),
                        history.getType().name(),
                        history.getMemo(),
                        history.getIpAddress(),
                        history.getModifiedAt()  // 여기서 'at' 필드에 해당 (요구사항에 따라 수정일시 선택)
                ))
                .collect(Collectors.toList());

        // 5. 다음 페이지 커서 계산
        Integer nextIdAfter = null;
        String nextCursor = null;
        if (hasNext) {
            // size+1번째 요소의 id를 기준으로 커서 생성
            EmployeeHistory nextHistory = pageResult.getContent().get(size);
            nextIdAfter = nextHistory.getId();
            nextCursor = Base64.getEncoder().encodeToString(("{\"id\":" + nextIdAfter + "}").getBytes());
        }

        // 6. 전체 건수 조회 (동적 조건에 따른)
        long totalElements = employeeHistoryRepository.count(spec);

        // 7. 응답 DTO 구성
        CursorPageResponseChangeLogDto<ChangeLogDto> response =
                new CursorPageResponseChangeLogDto<>(content, nextCursor, nextIdAfter, size, totalElements, hasNext);

        return response;
    }
}
