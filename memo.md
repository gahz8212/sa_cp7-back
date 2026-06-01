# Docker Database Setup & Usage Memo

이 문서는 프로젝트의 데이터베이스(MySQL 9.x)를 도커 환경에서 설정하고 관리하는 방법을 정리합니다.

## 1. Docker Desktop (Windows) 환경
윈도우 호스트에 도커 데스크탑을 설치하여 사용하는 방식입니다.

### 환경 분리 (추천)
- **Settings > Resources > WSL Integration**에서 사용 중인 WSL 배포판(Ubuntu 등)의 체크를 해제하여 WSL 내부 도커와 섞이지 않게 설정합니다.

### 실행 명령어 (PowerShell)
```powershell
docker run -d `
  --name cp-mysql `
  --restart always `
  -p 33306:3306 `
  -e MYSQL_ROOT_PASSWORD=di03367i `
  -e MYSQL_DATABASE=cp `
  mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

### 권한 및 인증 설정 (최초 1회 필수)
MySQL 9.x 버전의 외부 접속 허용을 위해 컨테이너 내부에서 아래 명령을 실행합니다.
```powershell
docker exec -it cp-mysql mysql -u root -pdi03367i -e "ALTER USER 'root'@'%' IDENTIFIED BY 'di03367i'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;"
```

---

## 2. WSL 내부 Docker (Native Linux) 환경
WSL(Ubuntu 등) 내부에 직접 설치된 도커 엔진을 사용하는 방식입니다.

### 실행 명령어 (WSL Terminal)
```bash
# 도커 서비스 시작
sudo service docker start

# MySQL 컨테이너 실행
docker run -d \
  --name cp-mysql \
  --restart always \
  -p 33306:3306 \
  -e MYSQL_ROOT_PASSWORD=di03367i \
  -e MYSQL_DATABASE=cp \
  mysql:latest --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

### 권한 및 인증 설정 (최초 1회 필수)
```bash
docker exec -it cp-mysql mysql -u root -pdi03367i -e "ALTER USER 'root'@'%' IDENTIFIED BY 'di03367i'; GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;"
```

---

## 3. 공통 확인 및 팁

### 한글 깨짐 방지 조회 (CLI)
조회 시 `--default-character-set=utf8mb4` 옵션을 사용합니다.
```bash
docker exec cp-mysql mysql -u root -pdi03367i --default-character-set=utf8mb4 -e "SELECT * FROM cp.excel_data;"
```

### 애플리케이션 연결 정보 (application.yml)
- **URL:** `jdbc:mysql://127.0.0.1:33306/cp?serverTimezone=Asia/Seoul&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false`
- **Username:** `root`
- **Password:** `di03367i`

---

## 4. Excel 업로드 타입 분석 (Type Analysis)

엑셀 파일 업로드 시 첫 번째 데이터 행을 기반으로 컬럼별 데이터 타입을 분석하여, 마지막 행으로 'TYPE' 타입의 데이터를 DB에 함께 저장합니다.

- **대상:** 첫 번째 데이터 행 (row_index = rowNo + 1)
- **로직:**
  - 헤더 처리 후 첫 번째 데이터 행을 분석하여 컬럼 타입을 결정 (STRING, NUMBER, BOOLEAN).
  - 전체 데이터 처리 완료 후, 분석된 타입 정보를 담은 `Excel` 객체를 생성하여 `ROW_TYPE`을 "TYPE"으로 설정하여 DB에 삽입.
- **용도:** 저장된 데이터의 타입 검증(Validation) 및 후속 데이터 처리 시 활용.

---

## 5. Backend API Specification

프론트엔드 연동을 위한 백엔드 API 명세입니다.

- **Base URL:** `http://localhost:8080` (개발 환경 기준)
- **API Endpoints:**
  - **Excel Upload:** `POST /common/upload-excel`
    - Content-Type: `multipart/form-data`
    - Payload: { file, sheetNo, rowNo }
    - Response: { uploadExcelKey, totalCount, dataList: [...] }

