## 엑셀 업로드 로직 개선 계획

## 현황 (2026-06-04 수정)
- 엑셀 업로드 시 데이터 유실 문제 해결 완료.
- `.xls`와 `.xlsx` 파일 형식 모두 지원.
- 밀도 기반 자동 헤더 탐색 로직 적용 (비어 있지 않은 셀이 가장 많은 행을 기준으로 함).
- 제목 행을 포함한 전체 행을 DB에 저장하여 데이터 완전성 확보.

## 완료 작업 (2026-06-04)
1. `WorkbookFactory`를 도입하여 `.xls`, `.xlsx` 형식 통합 지원.
2. `findBestHeaderRow` 메서드를 추가하여 자동 헤더 탐색 및 정렬.
3. 엑셀의 0번 행부터 전체 데이터를 DB에 저장하도록 `uploadExcel` 로직 수정.
4. `.xls` 파일(HTML 포맷 등) 업로드 시 발생하던 오류를 위한 라이브러리(`poi`) 추가 및 검증 로직 보강.

## 완료 작업 (2026-06-06)
1. `ExcelController`의 `@RequestBody` 임포트 오류 수정:
   - Swagger용 `@io.swagger.v3.oas.annotations.parameters.RequestBody`가 임포트되어 Spring MVC에서 JSON 요청 바디를 정상적으로 역직렬화하지 못하던 문제 해결.
   - `org.springframework.web.bind.annotation.RequestBody`로 변경하여 `UpdateExcelRequest` 데이터가 정상적으로 전달되도록 수정.
   - 이를 통해 "수정된 데이터 내역이 비어있습니다." 검증 에러 해결.
2. 엑셀 수정 데이터 업데이트 전략 결정:
   - DB 직접 업데이트 vs 세션/캐시 업데이트 비교 분석.
   - 실시간(Per-cell) 업데이트보다는 네트워크 효율과 UX를 고려하여 **버퍼링/일괄 동기화(Buffered/Batch Update)** 전략을 채택하기로 함.
   - 화면에서 즉시 변경 사항을 보여주는 UX(프론트엔드 연산)와 데이터 안정성(서버 캐시/DB 동기화)을 분리하여 설계.
3. 워크스페이스 간 기록 동기화 전략 수립:
   - 저장소가 분리된(Front/Back) 환경에서 정보 공유를 위해 각 루트의 `memo.md`에 동일한 주요 결정 사항을 기록함.
   - API 스펙 및 데이터 규약은 상호 참조하도록 관리.

## 완료 작업 (2026-06-07)
- 엑셀 업로드 후 조회 시 `size` 파라미터가 20으로 하드코딩되어 있던 문제 확인 (수정 예정).
- 엑셀 수정 사항 저장 API (`/api/common/save-excel-changes`) 동작 분석:
  - 컨트롤러 (`ExcelController.saveExcelChanges`)에서 요청 수신 및 `UpdateExcelRequest` 바인딩 확인.
  - 서비스 (`ExcelService.updateModifiedRows`)에서 현재 DB 반영 로직 없이 로그 출력만 수행하는 스텁(Stub) 상태임을 확인. 실제 구현 시 DB 업데이트 로직 필요.

## 엑셀 동적 템플릿 생성 및 검증 전략 (2026-06-09)
- JSON 기반 데이터 스키마 정의:
  - `flattenedHeaders`: 엑셀 헤더 레이아웃(좌표/병합) 정의.
  - `flattenedType`: 엑셀 유효성 검증 규칙(데이터 타입 및 정규식) 정의.
- DTO 구조 변경:
  - `ExcelCell` 클래스 신설 (`value`, `row`, `col`, `rowspan`, `type`, `pattern` 필드 포함).
  - `StructureExcelRequest`에서 `List<List<String>>` 대신 `List<ExcelCell>`을 사용하여 타입 안전성 확보.
- 검증 로직 구현:
  - 업로드/다운로드 시 `flattenedType` 스키마를 사용하여 데이터 타입 및 정규식 검증 로직 적용.
  - DB 조회 데이터를 `flattenedHeaders` 기반으로 레이아웃에 매핑하여 엑셀 파일 생성.
- 요약: 고정된 템플릿 파일 없이 프론트엔드에서 제공하는 스키마 정보와 DB 데이터를 결합하여 동적 생성 및 엄격한 데이터 검증 구현 완료.

## 엑셀 D&D 매핑 및 템플릿 관리 설계 (2026-06-12)

### 1. 핵심 용어 정의 (정확성 및 무결성 중심)
| 용어 | 필드명 | 의미 | 비고 |
| :--- | :--- | :--- | :--- |
| **시스템 고유 키** | `backColumn` | 백엔드 DB/Entity와 1:1 매핑되는 변하지 않는 고유 ID | 예: `memberName` |
| **표시용 라벨** | `name` | 관리자 화면(도착지 영역)에 노출될 친절한 한글 이름 | 예: 회원이름 |
| **엑셀 컬럼명** | `frontColumn` | 업로드된 엑셀 파일의 헤더(출발지 영역) 이름 | 예: 고객명 |

### 2. 업무 타입(targetSysType) 판별 및 메타데이터 제공 전략
- **추론 배제(Exact Match):** 금융/급여 데이터의 정확성(10억 단위 이상)을 위해 키워드 기반 추론을 배제하고 정확한 파일명 매핑 방식 채택.
- **파일명 매핑:** `ExcelService.getExactSysTypeByFileName`을 통해 지정된 파일명과 100% 일치할 때만 `targetSysType` 판별.
  - `근로자_간편서식.xlsx` -> `EMPLOYEE`
  - `급여대장_양식.xlsx` -> `PAYROLL`
- **통합 응답:** `upload-excel` API 호출 시, 업로드 데이터와 함께 해당 파일에 필요한 `targetColumns` (메타데이터 리스트)를 한 번에 리턴하여 프론트엔드 통신 최적화.

### 3. 데이터베이스 저장 구조 (excel_mapping_template)
사용자가 구성한 매핑 정보와 그리드 헤더 구조는 유연성을 위해 JSON 형태로 통합 저장함.
- **userMapping:** `[{"frontColumn": "고객명", "backColumn": "memberName"}, ...]`
- **headerStructure:** 그리드의 병합/계층 구조를 담은 JSON 객체.

### 4. 검증 및 보안
- **필수 매핑 강제:** `SysMetadataDto.required` 속성을 활용하여 필수 시스템 컬럼에 매핑된 엑셀 데이터가 없을 경우 저장 차단.
- **파일명 관리:** 관리자의 워크플로우를 존중하여 원본 파일명 기반의 엄격한 매칭을 유지하되, 추후 필요시 중복 다운로드 꼬리표(`(1)`) 제거 로직 검토 가능.

