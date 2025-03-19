package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.*;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmployeeHistoryMapper {

    public static EmployeeHistory toEntity(String employeeNumber, EmployeeHistory.ChangeType type, String memo,
                                           EmployeeCreateRequest changes, EmployeeDto afterData, String clientIp) {
        Map<String, Object> changedFields = new HashMap<>();

        if(changes != null) {
            changedFields.put("name", changes.getName());
            changedFields.put("email", changes.getEmail());
            changedFields.put("departmentId", changes.getDepartmentId());
            changedFields.put("position", changes.getPosition());
            changedFields.put("hireDate", changes.getHireDate());
        }

        return EmployeeHistory.builder()
                .employeeNumber(employeeNumber)
                .type(type)
                .memo(memo)
                .modifiedAt(LocalDateTime.now())
                .ipAddress(clientIp)
                .changedFields(changedFields)
                .build();
    }

    public static Map<String, Object> toDetailMap(DiffDto diff) {
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put("before", diff.getBefore());
        detailMap.put("after", diff.getAfter());
        return detailMap;
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
