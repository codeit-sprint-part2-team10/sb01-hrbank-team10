package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DiffDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeHistoryCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmployeeHistoryMapper {

    public static EmployeeHistory toEntity(EmployeeHistoryCreateRequest request, String clientIp) {
        List<DiffDto> changedFields = compareChanges(request.getBeforeData(), request.getAfterData());

        Map<String, Object> changedFieldMap = changedFields.stream()
                .collect(Collectors.toMap(
                        DiffDto::getPropertyName,
                        EmployeeHistoryMapper::toDetailMap
                ));

        return EmployeeHistory.builder()
                .employeeNumber(request.getEmployeeNumber())
                .type(request.getType())
                .memo(request.getMemo())
                .modifiedAt(LocalDateTime.now())
                .ipAddress(clientIp)
                .changedFields(changedFieldMap)
                .build();
    }

    private static Map<String, Object> toDetailMap(DiffDto diff) {
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put("before", diff.getBefore());
        detailMap.put("after", diff.getAfter());
        return detailMap;
    }

    private static List<DiffDto> compareChanges(EmployeeDto beforeData, EmployeeDto afterData) {
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

    public static ChangeLogDto toChangeLogDto(EmployeeHistory employeeHistory) {
        return ChangeLogDto.fromEntity(employeeHistory);
    }

    public static List<DiffDto> toDiffList(EmployeeHistory history) {
        if (history == null || history.getChangedFields() == null) {
            return Collections.emptyList();
        }
        return history.getChangedFields().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .map(entry -> {
                    Map<?, ?> detailMap = (Map<?, ?>) entry.getValue();
                    String before = detailMap.get("before") != null ? detailMap.get("before").toString() : "";
                    String after = detailMap.get("after") != null ? detailMap.get("after").toString() : "";
                    return new DiffDto(entry.getKey(), before, after);
                })
                .collect(Collectors.toList());
    }

}
