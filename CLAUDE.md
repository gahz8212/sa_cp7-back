# CP7 개발 컨벤션 (공유 룰)

CP7 멀티모듈 백엔드의 **개발 공통 규칙**입니다. 신규 코드는 이 문서를 따릅니다.
환경 구성은 [DEV_SETUP.md](DEV_SETUP.md) 참고.

- 스택: Spring Boot 3.3.5 / Java 21 / Gradle 멀티모듈
- 패키지 루트: `com.paycoms.cp7.<module>`
- 영속성: **MyBatis 전용** (JPA 미사용 — 아래 4절 참고)

---

## 1. 모듈 구조 & 배치 규칙

의존성 방향은 항상 단방향: **`global` ← `global-auth` ← 도메인 모듈**. 역방향/순환 의존 금지.

### 라이브러리 모듈 (`bootJar disabled`, jar만)
| 모듈 | 책임 |
|---|---|
| `global` | 공통 응답/예외/유틸 (ApiResponse, BaseTimeEntity, BusinessException, *Utils) |
| `global-auth` | JWT 인증 공통 (Security + Redis + jjwt) |
| `domain-nexus-{shinhan,jeju,openapi,unipost}` | 외부 연계 채널 (현재 빈 스캐폴드) |
| `domain-batch-shinhan` | 신한 배치 (Quartz + Spring Batch) |

### 실행 모듈 (`bootJar enabled`)
| 모듈 | 포트 | 책임 |
|---|---|---|
| `domain-api` | 8080 | 메인 비즈니스 API |
| `domain-admin` | 8083 | 관리자 API |
| `domain-nexus` | — | 외부 연계 통합 허브 |
| `domain-batch` | 8081 | 배치 실행 허브 |
| `domain-bankro` | 8082 (+TCP 17340) | 은행 TCP 소켓 통신 |
| `domain-watch` | — | 모니터링/스케줄러 |

### 기능을 어디에 둘까
| 작업 | 모듈 |
|---|---|
| 신규 비즈니스 API | `domain-api` |
| 관리자 화면용 API | `domain-admin` |
| 외부 은행/기관 연계 | `domain-nexus-<채널>` (통합 노출은 `domain-nexus`) |
| 스케줄 배치 | `domain-batch-<채널>` (실행은 `domain-batch`) |
| 실시간 모니터링/감시 | `domain-watch` |
| 은행 TCP 소켓 처리 | `domain-bankro` |
| 모듈 공통 유틸/예외/응답 | `global` |
| JWT/인증 공통 | `global-auth` |

---

## 2. 모듈 내부 레이어

```
controller / service / mapper(MyBatis 인터페이스) / model(Entity) / dto
```
패키지는 **`<기능>.<레이어>`** 구조입니다 — 기능(`auth`, `common`, `infrastructure`)/채널(`shinhan`, `jeju`)로 묶고,
그 아래 **레이어별 폴더**(`controller`/`service`/`mapper`/`model`/`dto`)로 분리합니다. ⚠️ 한 폴더에 service·mapper 를 섞지 말 것.
예: `com.paycoms.cp7.api.common.service.SequenceService`, `com.paycoms.cp7.api.common.mapper.SequenceMapper`,
`com.paycoms.cp7.api.auth.controller.AuthController`. Mapper XML 은 `src/main/resources/mapper/<기능>/*.xml`.

### 클래스 파일명 접미사 규칙 ⚠️
역할을 파일명으로 구분합니다. 신규 클래스는 반드시 준수:
| 역할 | 접미사 | 예 |
|---|---|---|
| API **요청** DTO | `...Request` | `LoginRequest` |
| API **응답** DTO | `...Response` | `TokenResponse` |
| 그 외 일반 DTO(전달용) | `...Dto` | `UserInfoDto` |
| **DB 쿼리 값**을 담는 모델(MyBatis 결과) | `...Entity` | `AuthEntity`, `UserCredentialEntity` |

> **DB 구조 은닉**: `Entity` 는 DB 컬럼명을 미러링(`idUsr`, `cdSys`, `pwLgn` …)하지만,
> API 계약인 `Request`/`Response`/`Dto` 는 **DB 컬럼명을 노출하지 말고 업무 의미 이름**을 씁니다
> (예: `id_usr`→`userId`, `cd_sys`→`systemCode`, `pw_lgn`→`password`, `id_sbjt`→`subjectId`, `nm_usr`→`userName`, `nm_sbjt`→`subjectName`).
> Entity↔DTO 변환은 service 계층에서 수행. JWT 클레임 키도 업무 이름을 사용.

---

## 3. 응답 & 예외 처리

- **모든 API 응답은 `ApiResponse<T>`** (status / code / message / data) 로 통일.
- 직접 만들지 말고 **`MessageUtils.createResponse(code, data, args...)`** 로 생성 (코드에서 status·message 를 i18n 으로 자동 추출).
- 비즈니스 오류는 **`throw new BusinessException(errorCode, args...)`**.
  `GlobalExceptionHandler`(`@RestControllerAdvice`)가 자동으로 `ApiResponse` 로 변환하므로 **컨트롤러에서 try/catch 금지**.
- **HTTP status 정책 ⚠️**: HTTP status = `ApiResponse.status` = 메시지코드의 `.status`.
  - **비즈니스 오류**(페이지에서 처리)는 `.status = 200` → 프론트가 **HTTP 200 으로 받고 `code` 로 분기** (예: `JOIN_*`, `AUTH_003`, `COMM_002/003`, `VALI_*`).
  - **시스템/세션 오류**만 실제 status(4xx/5xx): `SYS_*`, `COMM_001`(500), 세션/인가 게이팅 `AUTH_001`/`AUTH_002`(401), 토큰없음 `SYS_403`.
  - 신규 비즈니스 코드는 반드시 `.status=200` 으로 등록.
- 메시지 코드 체계 (`messages_ko.properties` / `messages_en.properties` 에 `<CODE>.status` / `<CODE>.message` 쌍으로 추가):
  - `SYS_xxx` — HTTP 상태
  - `VALI_xxx` — 입력 검증
  - `COMM_xxx` — 공통
  - `AUTH_xxx` — 인증/인가
  - 도메인별 — `EXCEL_xxx`, `FILE_xxx` 등

---

## 4. 영속성 — MyBatis 전용 ⚠️

> **JPA 는 2026-06-17 완전 제거되었습니다.** (`spring-boot-starter-data-jpa` 의존성 삭제)
> **`@Entity`, `JpaRepository`, `spring-boot-starter-data-jpa` 를 다시 추가하지 마세요.**

- Mapper 인터페이스: `com.paycoms.cp7.<module>.<feature>.mapper`
- Mapper XML: 각 모듈 `src/main/resources/mapper/<feature>/*.xml`
  (`mapper-locations: classpath:mapper/**/*.xml`)
- type-aliases-package: 모듈 루트(`com.paycoms.cp7.<module>`). 컬럼 `snake_case` → `camelCase` 자동 매핑 활성.
- Entity/모델은 `@Getter @Setter @NoArgsConstructor @Alias("...")` POJO. MyBatis 가 setter 로 채웁니다.
- 공통 시각 필드는 `BaseTimeEntity`(createdAt / updatedAt) 상속.
  **JPA Auditing 이 없으므로 값은 SQL `NOW()` 또는 매퍼/서비스에서 직접 세팅**해야 합니다 (insert/update 시 두 컬럼 누락 주의).
- 대량 insert 는 `ExecutorType.BATCH` 청크 처리 (`ExcelService` 참고).

---

## 5. 인증 & 인가 (`global-auth`)

JWT + Redis 무상태(stateless). 접근 제어는 **필터 + 인터셉터 + 어노테이션** 으로 동작 (SecurityConfig 자체는 `permitAll()`).

- **엔드포인트마다 정책을 명시**: `@Auth(AuthPolicy.X)`
  - `PUBLIC` — 인증 불필요 (공개 엔드포인트는 반드시 명시)
  - `AUTHENTICATED` — 유효 JWT 필요 (기본값)
  - `READ_WRITE` — 유효 JWT + 쓰기 권한(`readonly=false`) 필요. readonly 토큰이면 `AUTH_002` 로 차단
- 사용자 정보는 **`@LoginUser UserInfoDto user`** 파라미터로 주입 (id=email, name, serviceType, readOnly).
- 토큰: Access = `Authorization: Bearer` 헤더 / Refresh = Redis(`RT:{email}`) + HttpOnly 쿠키. 로그아웃은 access 토큰을 Redis 블랙리스트 등록.
- Swagger 경로(`/v3/api-docs/**`, `/swagger-ui/**` 등)는 필터/인터셉터에서 제외됨.

---

## 6. 빌드 & 의존성

- 공통 의존성(**MyBatis 3.0.5, Lombok, MySQL, MapStruct 1.5.5**)은 루트 `build.gradle` 의 `subprojects` 블록에서 자동 적용.
  → **모듈별 `build.gradle` 에 중복 선언 금지.**
- 라이브러리 모듈: `bootJar { enabled = false }` / `jar { enabled = true }`. 실행 모듈만 `bootJar { enabled = true }`.
- 모듈 간 의존은 `implementation project(':...')`, 방향은 1절 규칙 준수.
- 외부 HTTP 연계는 **OpenFeign** 클라이언트(`infrastructure.external` 패키지) 사용.

---

## 7. 공통 설정

- 모든 경로에 context-path **`/api`** 접두어 (`application-common.yml`). Swagger: `/api/swagger-ui.html`.
- 신규 Mapper XML 은 각 모듈 `src/main/resources/mapper/**` 에 배치.
- SQL 로깅은 p6spy 사용.

---

## 8. 보안 부채 (운영 전 정리 필요)

- JWT secret, `encrypt.key`(AES-256), DB·Redis 접속정보는 yml 에서 **환경변수 placeholder** 로 분리됨
  (`${JWT_SECRET:기본값}` 형태. 로컬 기본값은 `.env.example` 참고, 운영은 환경변수로 주입).
  ⚠️ **남은 작업**: git 에 남아 있는 기본값(이전 평문 secret)은 운영 배포 시 **반드시 새 값으로 로테이션**하고 yml 의 기본값 제거.
- CORS 가 전체 허용(`*`) 상태 → 운영 도메인으로 제한 필요.
- PoC/테스트 코드 잔존(예: `/auth/test`, bankro `ServerService` 응답 echo) → 운영 코드와 구분.

---

## 9. 테스트

- 테스트는 각 모듈 `src/test/java`, fixture 는 `src/test/resources` 에 둡니다.
- 공통 building block(응답/예외/유틸) 검증은 **Spring 컨텍스트 없이 순수 단위 테스트**로 작성합니다.
  참고 샘플: `global` 모듈의 `MessageUtilsTest`, `GlobalExceptionHandlerTest`
  (테스트 전용 `messages/*.properties` 번들 + `ResourceBundleMessageSource` 직접 구성).
- 비즈니스 예외는 `BusinessException` 을 던지고, 코드 → `ApiResponse` 변환은 `GlobalExceptionHandler` 가 담당하므로
  **컨트롤러가 아니라 핸들러/유틸 레벨에서 코드↔status↔message 매핑을 테스트**합니다.
- CI(`.github/workflows/ci.yml`)가 PR/푸시 시 `./gradlew build`(테스트 포함)를 실행합니다.

## 10. 헬스체크 / 모니터링

- Spring Boot Actuator 활성. 노출 엔드포인트는 `health`, `info` (`application-common.yml` 의 `management.*`).
- context-path 가 `/api` 이므로 헬스 경로는 **`/api/actuator/health`** (포트는 모듈별: api 8080 / batch 8081 / bankro 8082 / admin 8083).
- actuator 경로는 인증 필터(`JwtAuthenticationFilter`)·인터셉터(`WebConfig`)에서 **제외**되어 토큰 없이 접근 가능 (LB/모니터링용).
- `health.show-details: never` (상세 미노출). 상세가 필요하면 별도 인가 정책 검토 후 변경.

## 11. DB 스키마 컨벤션 (cp7) ⚠️

cp7 DB 는 구 시스템에서 마이그레이션된 스키마로, **헝가리안 접두어 네이밍**과 특유의 공통 규칙을 따릅니다. 신규 매퍼/엔티티는 반드시 준수.

> ⚠️ **스키마는 계속 변경됩니다 — 매퍼/엔티티 작성 전 반드시 실제 DB 스키마를 확인할 것.**
> 문서/덤프/메모리의 컬럼 정보는 *시점 기록*이라 신뢰하지 말고, 개발 시마다
> `SELECT column_name,column_type,column_comment FROM information_schema.columns WHERE table_schema='cp7' AND table_name='<t>'`
> (또는 `SHOW FULL COLUMNS`) 로 **컬럼명·코드값을 확인 후** 작성. (예: `cp_sbjt_acct` 의 신탁유형은 `tp_acct_trst`(구덤프) 가 아니라 실제 `tp_acct_knd`, `tp_acct_tgt` 값은 `'10'`.)

- **컬럼 접두어** = 데이터 의미/타입:
  `id_`식별자 · `cd_`코드 · `sn_`일련번호 · `no_`번호/횟수 · `nm_`명 · `tp_`유형 · `st_`상태 ·
  `am_`금액(`decimal(15,2)`) · `yd_`**일자(`int` YYYYMMDD)** · `ym_`연월(`char(6)`) · `ts_`일시(`datetime`) ·
  `yn_`**여부(`char(1)` Y/N)** · `tx_`텍스트 · `ad_`주소. 테이블: `cp_`=운영, `mig_`=마이그레이션 임시(**운영 로직 사용 금지**).
- **`cd_sys`(시스템코드)가 거의 모든 PK 에 포함** → 모든 조회/수정 쿼리 조건에 **반드시 포함**(멀티시스템 구분). 누락 시 타 시스템 데이터 혼입.
- **soft delete**: 조회 시 `yn_del = 'N' AND yn_use = 'Y'` 필터 필수.
- **감사 컬럼은 `id_reg`/`ts_reg`/`id_chg`/`ts_chg`** — `BaseTimeEntity`(createdAt/updatedAt)와 **이름이 다름**.
  → 이 스키마 테이블에는 BaseTimeEntity 를 그대로 쓰지 말 것. JPA Auditing 도 없으므로(§4) insert/update 시 `NOW()` + 등록자/변경자 **직접 세팅**.
- **날짜가 `int`(YYYYMMDD)·`char(6)`(YYYYMM)** — `LocalDate` 아님. 변환 유틸로 처리.
- **공통코드 `cp_cmn_cd`/`cp_cmn_cd_grp` ⚠️**: 코드그룹 키(`cd_grp`)가 곧 **컬럼 필드명**(`st_sbjt`, `tp_sbjt`, `tp_auth_usr` …).
  → 코드성 컬럼(`cd_*`/`tp_*`/`st_*`)의 값을 정하거나 검증할 땐 **임의 값 쓰지 말고 반드시 `cp_cmn_cd WHERE cd_grp='<필드명>'` 를 먼저 확인**해서 등록된 코드값을 사용.
  예: `st_sbjt` 01임시/02승인대기/03활성/04정지 · `tp_auth_usr` 01일반/02서브관리자/03총괄관리자/99임시사용자 · `tp_sbjt` CO/EQ/MT/WR · `tp_trms_cons` 01필수/02선택. (코드그룹이 없는 컬럼도 있음 — 예 `st_usr`.)
- **중심 엔티티 `cp_sbjt`**(회사·근로자·장비·자재 통합 "주체"). 흐름: `cp_ctrt`(계약) → `cp_bill`(청구) → `cp_pmt`(지급, 대용량) → `cp_dpst`(입금).
- **로그인은 `cp_usr`** 사용: `pw_lgn`(비번 해시 — §5 / `EncryptionUtils.hashPassword`), `no_lgn_fail`/`yn_pw_tmpr`/`yd_pw_chg`(계정정책), 권한은 `cp_usr_auth`+`tp_auth_usr`, 이력은 `cp_lgn_hstr`.
  ⚠️ `id_lgn` 컬럼은 **삭제 예정** — 매퍼/엔티티에서 참조 금지. 로그인 식별자는 `id_usr` 또는 `ad_email` 사용(확정 예정).
- **채번(시퀀스)은 `cp_sn_seq`를 직접 건드리지 말고 DB 채번 함수 `F_GET_SN(cd_task, cd_sys, org_cd)` 를 호출** — Java 에서 seq 증가/조합 직접 구현 금지.
  여러 도메인 공통이므로 **공통 채번 서비스(`SequenceService`, `@Transactional(REQUIRES_NEW)`)** 로만 호출. 회사 주체 `id_sbjt` = `CO`+YYYYMM+일련번호(tp_seq=2 월별).

---

## 12. API URL / 네이밍 규칙 ⚠️

### API URL
- 모두 `/api` prefix(context-path) + **kebab-case** 소문자. 행위는 URL 동사 대신 **HTTP 메서드**로 표현.
- 경로: `/{도메인}/{리소스}/{식별자}/{하위|액션}` — 1st segment=**도메인(기능, 단수, 패키지명 일치)**, 리소스 컬렉션=**복수 명사**(`/companies`, `/users`, `/terms`), 단건=`/{id}`.
- **CRUD = HTTP 메서드** (URL 에 get/create 금지):
  목록 `GET /member/companies` · 단건 `GET /member/companies/{id}` · 생성 `POST /member/companies` · 수정 `PUT|PATCH /…/{id}` · 삭제 `DELETE /…/{id}`.
- **비-CRUD 액션**은 하위에 동사 허용: `POST /member/join`, `POST /member/companies/{id}/activate`, 중복/유효성은 `GET …/check-id`·`check-business-number`.
- 검색/필터/페이징은 쿼리 파라미터(`page`,`size`,`sort`,`keyword`).
- **예외(레거시 유지)**: 인증 액션 `POST /auth/login`, `POST /auth/reissue`, `GET /auth/logout` 은 현행 경로 유지.

### 클래스 파일명
컨트롤러 `<도메인>Controller` · 서비스 `<도메인>Service` · 매퍼 `<도메인>Mapper`(+`.xml`) · 요청 `<액션>Request` · 응답 `<액션>Response` · 전달 `<의미>Dto` · DB모델 `<업무명>Entity` · 상수 `<의미>Constants`. (2절 접미사 규칙과 일치)

### 메소드 네이밍 (계층별 어휘 분리)
- **Controller**(짧게, 위임): `getTerms`, `getCompany`, `createCompany`, `updateCompany`, `deleteCompany`, `login`, `join`, `checkUserId`.
- **Service**(비즈니스 의도): 단건 `get`(없으면 예외)/`find`(없으면 null) · 목록 `getXxxList`/`searchXxx` · 존재 `exists` · 생성 `create`/`register` · 수정 `update` · 삭제(soft) `remove`/`delete` · 검증 `validate` · 상태변경 동사(`activate`/`suspend`).
- **Mapper(MyBatis)**(SQL 동작): `selectXxx`(단건)/`selectXxxList`(목록)/`insertXxx`/`updateXxx`/`deleteXxx`/`countByXxx`/`existsByXxx`.
- **Boolean**: `is`/`has`/`exists`/`can` 접두.
- 변수/필드/메서드는 **업무 의미 이름**(DB 컬럼명 노출 금지 — Entity 제외), 상수는 `UPPER_SNAKE_CASE`.
