# 👨🏻‍💻 **PART2_10팀**
# 🏦 HR Bank : Batch로 인사 데이터를 안전하게 관리하는 Open EMS
## **프로젝트 링크**
- ⭐ 프로젝트 배포 : https://sb01-hrbank-team10-production-6d34.up.railway.app/dashboard
- 🔗 프로젝트 문서 (다른 모든 프로젝트 링크들) : https://bit.ly/4c1zlg0

## **팀원 구성**
| 이름 | 이메일 | GitHub |
|------|--------|--------|
| **연예림** | yinneu@gmail.com | [github.com/yinneu](https://github.com/yinneu) |
| **변희재** | pion0458@gmail.com | [github.com/Heyaaz](https://github.com/Heyaaz) |
| **전민기** | mingi3070@gmail.com | [github.com/mingi96](https://github.com/mingi96) |
| **정연경** | edaily0129@gmail.com | [github.com/Yeonkyung-Jeong](https://github.com/Yeonkyung-Jeong) |
---

## **프로젝트 소개**
### **< Open EMS(Enterprise Management System) ‘HR Bank’의 Spring 백엔드 시스템 구축 >**

🕛 **프로젝트 기간**: 2025.03.13 ~ 2025.03.24  

💭 **Abstract - 프로젝트 고안 이유**

-- 최근 많은 기업들이 **인사 데이터를 효율적으로 관리**하지 못해 운영 효율성과 경쟁력이 저하되는 문제를 겪고 있다. 특히 급격히 늘어난 직원 정보와 복잡한 변경 이력 관리, 데이터 손실 위험 등으로 인해 인사 관리 업무가 복잡해지고 있는 현상이 늘어났다.

HR Bank는 이러한 현실적인 어려움을 해결하고자 **기업의 인적 자원을 안정적으로 관리하고 데이터 기반의 신속한 의사결정을 지원할 수 있는 통합 시스템**으로서 고안되었다.

🖊️ **프로젝트 내용 요약**

- **부서 및 직원 정보 관리:** 체계적인 부서 및 직원 데이터의 등록, 수정, 삭제, 조회 기능 제공
- **대량 데이터 처리 (Batch 시스템):** 안정적인 데이터 관리 및 신속한 처리 지원
- **백업 자동화:** 중요 인사 데이터의 정기적인 백업 및 복원 기능 제공
- **이력 관리:** 직원 데이터 변경 사항의 상세한 이력 기록 및 관리
- **대시보드 제공:** 인적 자원 현황을 한눈에 파악할 수 있는 직관적 데이터 시각화 제공 <br>
💡 **Index**
(*효율적 인사 관리 / Batch 시스템 / 데이터 안정성 / 자동 백업 / 이력 관리 / 대시보드*)
---
## **기술 스택**
💻 **Backend**
- Spring Boot
- Spring Data JPA
- Springdoc-openapi
- Railway.io
- Specification

💽 **Database**

- PostgreSQL

⚡ **기타 협업 Tool**

- Git & Github
- Jira
- Slack
- Notion
- Discord
---

## **팀원별 구현 기능 상세**
### 👤 연예림

![image](https://github.com/user-attachments/assets/4f849541-c75e-45ac-b070-1386b13e5a89)

![image](https://github.com/user-attachments/assets/1a2f56de-1d4e-4c77-87e6-d1a6d3e1b544)


- **직원 관리 API**
    - 직원 정보 CRUD 구현 (Spring Data JPA 사용)
    - 직원 프로필 이미지 업로드 및 관리
    - 직원 목록 및 상세 조회 (Specification 기반 동적 쿼리 생성, 정렬 및 커서 페이지네이션)
- **대시보드 API**
    - 주요 지표 제공 (총 직원 수, 최근 수정 이력 건수, 이번달 입사자 등)
    - 최근 1년 직원 변동 추이, 부서 및 직무별 직원 분포 등 데이터 시각화 API 제공
- **인프라**
    - Railway DB 설정
    - Railway 활용한 서비스 배포

### 👤 변희재

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **부서 관리 API**
    - 부서 정보 CRUD 구현 (Spring Data JPA 사용)
    - 부서 목록 조회 (다양한 조건 검색, 정렬 및 커서 페이지네이션)
- **대시보드 API**
    - 주요 지표 제공 (총 직원 수, 최근 수정 이력 건수, 이번달 입사자 등)
    - 최근 1년 직원 변동 추이, 부서 및 직무별 직원 분포 등 데이터 시각화 API 제공

### 👤 전민기

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

- **직원 정보 수정 이력 관리 API**
    - 직원 정보 수정 시 이력 자동 기록 (변경된 데이터만 저장)
    - 수정 전/후 데이터 차이 기록 및 관리
    - 직원 추가, 정보 수정, 삭제 유형별 이력 구분 및 처리
    - 이력 목록 조회 및 상세 이력 조회 기능 (다양한 조건 검색, 정렬 및 커서 페이지네이션)

### 👤 정연경
![image](https://github.com/user-attachments/assets/66fa7070-0aea-433c-9167-69c3cad2f968)

![image](https://github.com/user-attachments/assets/19c7b545-c850-433c-9d4a-8f44c6cc2a1f)


- **파일 관리 API**
    - 바이너리 CRUD 구현 (Spring Data JPA 사용)
    - 파일 메타 정보, 실제 파일 분리 저장 처리
    - 파일 업로드, 저장, 다운로드 기능 구현
- **데이터 백업 관리 API**
    - 데이터 백업 구현
    - 데이터 자동 배치 작업 구현 (Spring Scheduler 기반)
    - 데이터 백업 이력 목록 조회 및 관리 (정렬 및 커서 페이지네이션)
---

## 📂**파일 구조**
```
src
└── main
    ├── java
    │   └── sb01part2hrbankteam10
    │       ├── config
    │       │   ├── ModelMapperConfig.java
    │       │   ├── SchedulerConfig.java
    │       │   └── SwaggerConfig.java
    │       ├── controller
    │       │   ├── docs
    │       │   │   ├── BackupDocs.java
    │       │   │   ├── BinaryContentDocs.java
    │       │   │   ├── DepartmentDocs.java
    │       │   │   ├── EmployeeDocs.java
    │       │   │   └── EmployeeHistoryDocs.java
    │       │   ├── api
    │       │   │   ├── BackupController.java
    │       │   │   ├── BinaryContentController.java
    │       │   │   ├── DepartmentController.java
    │       │   │   ├── EmployeeController.java
    │       │   │   └── EmployeeHistoryController.java
	  │       │   ├── view
    │       │   │   └── ViewController.java
    │       ├── dto
    │       │   ├── backup
    │       │   │   ├── BackupDto.java
    │       │   │   ├── CursorPageResponseBackupDto.java
    │       │   │   └── EmployeeForBackupDto.java
    │       │   ├── binary_content
    │       │   │   └── BinaryContentUploadResponse.java
    │       │   ├── department
    │       │   │   ├── DepartmentCreateRequest.java
    │       │   │   ├── DepartmentDto.java
    │       │   │   ├── DepartmentResponseDto.java
    │       │   │   └── DepartmentUpdateRequest.java
    │       │   ├── employee
    │       │   │   ├── EmployeeCreateRequest.java
    │       │   │   ├── EmployeeDistributionDto.java
    │       │   │   ├── EmployeeDto.java
    │       │   │   ├── EmployeeHistoryCreateRequest.java
    │       │   │   ├── EmployeeSearchRequest.java
    │       │   │   ├── EmployeeTrendDto.java
    │       │   │   └── EmployeeUpdateRequest.java
    │       │   ├── employee_history
    │       │   │   ├── ChangeLogDto.java
    │       │   │   ├── CursorPageResponseChangeLogDto.java
    │       │   │   └── DiffDto.java
    │       │   └── page
    │       │       └── CursorPageResponseDto.java
    │       ├── entity
    │       │   ├── Backup.java
    │       │   ├── BinaryContent.java
    │       │   ├── Department.java
    │       │   ├── Employee.java
    │       │   └── EmployeeHistory.java
    │       ├── global
    │       │   ├── converter
    │       │   │   └── JsonConverter.java
    │       │   ├── exception
    │       │   │   ├── errorcode
    │       │   │   │   └── ErrorCode.java
    │       │   │   ├── ErrorResponse.java
    │       │   │   ├── GlobalExceptionHandler.java
    │       │   │   └── RestApiException.java
    │       │   └── mapper
    │       │       ├── BackupMapper.java
    │       │       ├── DepartmentMapper.java
    │       │       ├── DepartmentUpdateMapper.java
    │       │       ├── EmployeeHistoryMapper.java
    │       │       └── EmployeeMapper.java
    │       ├── repository
    │       │   ├── specification
    │       │   │   └── EmployeeSpecification.java
    │       │   ├── BackupRepository.java
    │       │   ├── BinaryContentRepository.java
    │       │   ├── DepartmentRepository.java
    │       │   ├── EmployeeHistoryRepository.java
    │       │   └── EmployeeRepository.java
    │       ├── service
    │       │   ├── impl
    │       │   │   ├── BackupServiceImpl.java
    │       │   │   ├── BinaryContentServiceImpl.java
    │       │   │   ├── DepartmentServiceImpl.java
    │       │   │   ├── EmployeeHistoryServiceImpl.java
    │       │   │   ├── EmployeeServiceImpl.java
    │       │   │   └── EmployeeStatServiceImpl.java
    │       │   ├── BackupService.java
    │       │   ├── BinaryContentService.java
    │       │   ├── DepartmentService.java
    │       │   ├── EmployeeHistoryService.java
    │       │   ├── EmployeeService.java
    │       │   └── EmployeeStatService.java
    │       ├── storage
    │       │   ├── impl
    │       │   │   └── BinaryContentStorageImpl.java
    │       │   └── BinaryContentStorage.java
    │       ├── util
    │       │   └── IpUtil.java
    │       └── Sb01Part2HrbankTeam10Application.java
    └── resources
           └──application.yml
```
---

## 🌐 **계층 구조**
```
Client
  │ HTTP Request / Response
  ▼
┌───────────────────────────────────┐
│ Presentation Layer (Controller)   │ ──→ API Docs (Swagger)
└───────────────┬───────────────────┘
                │ DTO
┌───────────────▼───────────────────┐
│ Application Layer (Service Impl)  │
└───────────────┬───────────────────┘
                │ Entity, DTO
┌───────────────▼───────────────────┐
│      Domain Layer                 │
│   (Entity, DTO, Mapper)           │
└───────────────┬───────────────────┘
                │ Entity
┌───────────────▼───────────────────┐
│ Infrastructure Layer              │
│ (Repository, Storage, Spec)       │ ──→ Storage
└───────────────┬───────────────────┘
                │ Database CRUD
                ▼
         Database (PostgreSQL)
```
---
## 📋 **배포 다이어그램**
![image](https://github.com/user-attachments/assets/9f8cb677-280c-41e1-9020-c1f7bfffbcf8)
## ☁️ **ERD**
![image](https://github.com/user-attachments/assets/b6f32614-0b67-4d77-8e1f-220a2f25d344)

| 문제상황 | 원인 | 해결 방법 | 배운 점 |
| --- | --- | --- | --- |
| 직원 생성 시 이력 생성 실패 | beforeData 누락 | 파라미터를 EmployeeCreateRequest로 변경 | 생성 로직에서도
이력 처리 필수 |
| 변경되지 않은 직원 생성 시 오류 | null 참조로 인한
예외 | null 체크 추가 | null 체크의 중요성 재확인 |
| 부서명 검색 오류 | 검색 조건 처리 미흡 | like 검색 방식 보완 | 필터 기능에 대한 검증 필요 |
| ChangeLog JSONB
필드 매핑 오류 | 매핑 누락 | 매핑 로직 수정 | JSONB 타입 활용시
매핑 주의 |
| 중복 시작, 시작 시간 필터 오류 | 쿼리 조건 처리 미흡 | @DateTimeFormat 및
기본값 보완 | API 파라미터 처리 시
예외 케이스 고려 필수 |
| 백업 실행되지 않았을 때 NPE | null 체크 누락 | status 값이 null일 경우
빈 DTO 반환 | null 체크의 중요성  |
| 프로필 적용 시 이미지 파일 외 타입 허용 문제 | 파일 타입 검증 누락 | validateFileExtension
메서드 추가 | 입력값 검증의 중요성  |
| 동적 쿼리 구현 복잡성 | JPQL만으로 처리
어려움 | Specification 패턴 적용 | 동적 쿼리 구현 기법
습득 |
| 브랜치 관리 문제 | 브랜치 설정 오류 | main→dev PR 미리 close | 브랜치 전략 수립
중요성  |
| 환경변수 설정 문제 | 개발/배포 환경 분리 미흡 | 환경별 yml 분리 및
intellij 환경변수 활용 | 환경 설정 분리의
중요성 |

