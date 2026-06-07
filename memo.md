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

## 향후 작업 계획 (2026-06-05 예정)
1. 템플릿 파일 생성 및 관리 체계 구축.
2. 신규 업로드 파일에 대한 템플릿 기반 데이터 검증 로직 구현.
3. `file_key`를 원본 파일명 기반으로 할당하는 로직 적용.
