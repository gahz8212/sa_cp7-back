# CP7 백엔드 개발 로드맵 (V0 기반)

> V0 프론트(4개 Next.js 프로토타입) + `API_도출목록.md`(~200 엔드포인트) ↔ cp7 DB(61테이블) 매핑 분석.
> 작성: 2026-06-23 · 기준: V0/API_도출목록.md, cp7 스키마, CLAUDE.md 컨벤션.

---

## 1. V0 성격
- 4개 앱(계약·현장 / 기본정보·지급현황 / 대금청구 / 청구승인·대금지급)은 **단일 도메인의 단계 분할**: `현장 → 계약 → 청구 → 승인 → 입금확인 → 지급`.
- 전부 **Zustand 목업**(실제 API 없음) → 화면 의도로 역추론한 API 명세. **백엔드는 0부터, 화면이 명세 역할**.
- API 도출목록은 이미 **업무명(camelCase)** 으로 작성 → 우리 "DB 은닉" 규칙과 일치 → Request/Response 명으로 차용 가능.

## 2. API 영역 ↔ DB 테이블 매핑 / 커버리지

| API 영역 | 핵심 DB 테이블 | 상태 |
|---|---|---|
| 인증·회원가입 | cp_usr, cp_sbjt, cp_usr_auth, cp_lgn_hstr | ✅ login/join/crosscert 완료, `/auth/me`·SMS 미구현 |
| 현장 Site | cp_site | ⬜ |
| 계약/하위계약 | cp_ctrt (+grp/rltp/sign/elct) | ⬜ |
| 청구 Billing | cp_bill (+aprv_dtl/hier/prpy_dtl/dpst_mtch/sign…) | ⬜ |
| 승인 워크플로 | cp_bill.st_aprv/st_bill + cp_sgnf/_dtl/_line/_line_dtl (전자결재) | ⬜ |
| 입금확인·매핑 | cp_dpst, cp_bill_dpst_mtch | ⬜ |
| 대금지급 | cp_pmt (대용량), cp_pmt_ldgr | ⬜ |
| 압류/증명서 | cp_szr/_ctrt (증명서=문서발급) | ⬜ |
| 항목 마스터(근/장/재) | cp_sbjt(WRKR/EQP_CO/MTRA_CO), cp_sbjt_mbr, cp_sbjt_ocpt, cp_sbjt_acct | ⬜ |
| 검색/룩업 | cp_sbjt, cp_usr, cp_cmn_cd, 주소(외부) | 🔶 보조검증만 |
| 계좌 Account | cp_sbjt_acct, cp_cmn_acct_vrfc | ⬜ |
| 설정(회사/부서/사용자) | cp_sbjt, cp_cmn_dept/_usr, cp_usr/_auth, cp_intd_optn/_ui_optn, cp_auth_prst/_dtl | ⬜ |
| 통계/대시보드 | 집계(cp_bill/cp_pmt/cp_ctrt) | ⬜ |
| 메뉴/공통코드 | cp_menu, cp_cmn_cd/_grp | ⬜ |

## 3. 갭 / 먼저 합의할 결정사항

| # | 항목 | 내용 | 결정 필요 |
|---|---|---|---|
| 1 | role vs adminLevel | API `role(owner/prime/sub)`=계약상 지위(cp_ctrt.id_buyr/id_selr에서 파생), `adminLevel(총괄/서브/일반)`=cp_usr.tp_auth_usr. 서로 다른 축 | 스코핑/권한 설계 기준 |
| 2 | e계좌(eAccountNo) | 단일 컬럼 불명확 — cp_dpst.no_elct_acct / cp_sbjt_acct(신탁) 조합 추정 | e계좌 모델 확정 |
| 3 | 대출선지급 / 전자카드제 | 대응 테이블 없음 | 신규 테이블/외부연동 여부 |
| 4 | ChangeLog(변경이력) | API는 범용 `*/history`. DB는 테이블별 `*_hstr`/감사컬럼 | 범용 이력테이블 vs 테이블별 |
| 5 | 채번 cd_task 매핑 | cp_sn_seq에 CT/BI/CL/PM 등 존재 → 계약=CT, 청구=BI? | 업무코드↔리소스 매핑표 |
| 6 | 상태머신 코드값 | 청구등록→승인→지급가능→지급완료 + 분기 → st_bill/st_aprv/st_pmt 값 | cp_cmn_cd에서 코드 확정 |

## 4. 공통 토대 (모든 화면의 전제 — 먼저 정립)
- **토큰 스코핑**: 클레임 subjectId(회사)+계약지위로 buyer/seller 필터 (공통 인터셉터/유틸).
- **페이징·검색 표준**: page/size/sort/keyword + ApiResponse 페이지 래퍼.
- **공통코드 조회 API** (cp_cmn_cd by cd_grp) — 화면 드롭다운 전부 의존.
- **채번 cd_task 매핑** 확정 (SequenceService 이미 존재).
- **권한(tp_auth_usr) 게이팅** — @Auth 확장 검토.

## 5. 단계별 로드맵 (의존 순서)

| Phase | 범위 | 근거 |
|---|---|---|
| 0 (완료) | auth(login/logout/reissue), member(join+보조검증), crosscert | ✅ |
| 1 기반 | 공통코드 조회 · `/auth/me` · 회사/부서/사용자 설정 · 계좌 · 검색/룩업 | 다른 화면 드롭다운·스코핑 전제 |
| 2 골격 | 현장(Site) → 계약/하위계약(Contract) | 청구의 부모, 채번·e계좌 발급 |
| 3 청구 | 청구(Billing) CRUD + 항목 마스터(근/장/재/기타) | 핵심 업무 |
| 4 승인 | 청구승인 워크플로(cp_sgnf 결재) + 상태머신 | 청구 의존 |
| 5 지급 | 입금확인·매핑 → 대금지급(cp_pmt) → 거래내역 | 승인 의존, 대용량 |
| 6 부가 | 압류/증명서 · 통계/대시보드 · 외부연동(SMS/실명/대출/카드) | 마무리 |

## 6. 다음 액션
1. **Phase 1의 "공통코드 조회 API + `/auth/me`"** 부터 — 작고, 모든 화면 의존, DB 이미 존재.
2. 병행해 **3절 갭 6개 결정** (특히 role 파생 규칙, 채번 cd_task 매핑, e계좌 모델) → Phase 2~5 설계 전제.
