# CP7 Project Rules

모든 개발 진행 시 아래 규칙과 프로젝트 루트의 [CLAUDE.md](file:///home/ksh/cp7/standAlone/sa_cp7-back/CLAUDE.md) 컨벤션을 철저히 준수해야 합니다.

## 1. 기술 스택 & 구조
- **Spring Boot 3.3.5 / Java 21 / Gradle 멀티모듈**
- **MyBatis 전용 (JPA 절대 금지)**: JPA 관련 어노테이션(`@Entity`, `@Table` 등)이나 JpaRepository, `spring-boot-starter-data-jpa`를 추가하지 마세요.
- **모듈 의존성 단방향 유지**: `global` ← `global-auth` ← 도메인 모듈 (`domain-api`, `domain-admin` 등). 순환 의존 금지.

## 2. 패키지 및 클래스명 규칙
- **패키지 구조**: `com.paycoms.cp7.<module>.<기능/도메인>.<레이어>` (예: `controller`, `service`, `mapper`, `model`, `dto` 폴더 분리)
- **클래스명 접미사**:
  - API 요청 DTO: `...Request`
  - API 응답 DTO: `...Response`
  - 일반 DTO (전달용): `...Dto`
  - DB MyBatis 매핑 모델: `...Entity`
- **DB 구조 은닉**: `Entity`에만 DB 컬럼명(헝가리안 스타일)을 매핑하고, DTO(Request/Response/Dto)는 DB 컬럼명을 그대로 노출하지 말고 **업무적 의미의 camelCase** 이름을 사용하세요. 변환은 Service 계층에서 수행합니다.

## 3. 응답 & 예외 처리
- **API 응답**: 모든 API 응답은 `ApiResponse<T>`로 통일하며, 직접 생성하지 않고 `MessageUtils.createResponse(code, data, args...)`로 생성합니다.
- **예외 처리**: 비즈니스 예외는 `throw new BusinessException(errorCode, args...)`로 발생시킵니다. `GlobalExceptionHandler`가 자동 처리하므로 **컨트롤러에서 try/catch 금지**.
- **HTTP status**: 비즈니스 예외는 프론트엔드가 code로 분기할 수 있도록 HTTP 200(ok)으로 응답해야 합니다. 시스템/세션 오류(4xx, 5xx)인 경우에만 실제 HTTP 상태 코드를 사용합니다.

## 4. 인증 & 인가
- JWT + Redis 무상태 인증을 사용합니다.
- 컨트롤러의 각 엔드포인트마다 `@Auth(AuthPolicy.PUBLIC)`, `@Auth(AuthPolicy.AUTHENTICATED)`, `@Auth(AuthPolicy.READ_WRITE)` 정책을 반드시 명시해야 합니다.
- 사용자 정보는 컨트롤러 파라미터에서 `@LoginUser UserInfoDto user`로 주입받습니다.

## 5. DB 스키마 컨벤션 (cp7)
- **접두어 네이밍**: 헝가리안 표기법 준수 (`id_` 식별자, `cd_` 코드, `sn_` 일련번호, `no_` 번호/횟수, `nm_` 명, `tp_` 유형, `st_` 상태, `am_` 금액, `yd_` 일자(int), `yn_` 여부(char 1 Y/N), `ts_` 일시).
- **시스템 코드**: 모든 조회/수정 쿼리 조건에 `cd_sys`를 필수적으로 포함해야 합니다.
- **Soft Delete**: 조회 쿼리 시 `yn_del = 'N' AND yn_use = 'Y'` 조건을 필수로 지정해야 합니다.
- **감사 필드**: `id_reg`, `ts_reg`, `id_chg`, `ts_chg` 컬럼을 사용하며, SQL `NOW()` 또는 매퍼/서비스에서 직접 값을 세팅해야 합니다 (JPA Auditing 미사용).
- **채번 (시퀀스)**: Java에서 시퀀스를 증가시키지 말고, DB 함수 `F_GET_SN(cd_task, cd_sys, org_cd)`를 `SequenceService`를 통해 호출해 사용해야 합니다.

## 6. API URL 및 네이밍 규칙
- **API URL**: `/api` 접두어 필수, kebab-case 소문자 사용.
- **RESTful**: 행위는 URL에 get/create 등의 동사를 쓰는 대신 HTTP Method(GET, POST, PUT, DELETE)로 표현합니다.
- **메서드 네이밍**:
  - Controller: `getTerms`, `createCompany` 등 (짧게 위임)
  - Service: `get`/`find` (단건), `getXxxList`/`searchXxx` (목록), `create`/`register` (생성), `update` (수정), `remove`/`delete` (삭제)
  - Mapper: `selectXxx`, `selectXxxList`, `insertXxx`, `updateXxx`, `deleteXxx`
