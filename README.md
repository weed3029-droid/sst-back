# 경기도 스마트 여행 플래너 — Backend

> **팀명**: 식스센스 (SixSense) · **팀원**: 김영훈, 김지태, 노송현, 소제우, 손상진, 한상인

경기도 내 여행지 정보를 제공하고, 사용자 선택에 따라 AI가 동선 최적화된 여행 일정을 자동으로 생성해주는 웹 서비스의 **Java Spring 백엔드** 레포지토리입니다.

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [주요 기능](#주요-기능)
- [데이터베이스 구조](#데이터베이스-구조)
- [API 엔드포인트](#api-엔드포인트)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [환경 변수](#환경-변수)
- [팀원 소개](#팀원-소개)

---

## 프로젝트 소개

**경기도 스마트 여행 플래너**는 경기도 내 다양한 여행지(볼거리·먹거리·놀거리·잘거리) 정보를 제공하며, 사용자가 지역·기간·테마를 선택하면 **GPT-4o 기반 AI가 동선 최적화된 여행 일정을 자동 생성**해주는 플랫폼입니다.

사용자는 AI가 추천한 일정을 기반으로 장소를 추가·수정·저장하고, 자신의 여행 이야기를 커뮤니티에 공유할 수 있습니다.

| 구분 | 내용 |
|------|------|
| 개발 기간 | 2025.12.29 ~ 2026.06.23 |
| 서비스 대상 | 경기도 여행을 계획하는 일반 사용자 |
| 배포 환경 | OCI (Oracle Cloud Infrastructure) |

---

## 기술 스택

### Backend
| 분류 | 기술 |
|------|------|
| Language | Java 21 (OpenJDK) |
| Framework | Spring Framework, Spring Boot |
| Security | Spring Security + JWT (Access/Refresh Token) |
| ORM | MyBatis 3.x |
| Build | Maven / Gradle |
| Server | Apache Tomcat 10.1.x |

### Database & Infra
| 분류 | 기술 |
|------|------|
| DBMS | MySQL 8.0.26 |
| Cloud | OCI (Oracle Cloud Infrastructure) |
| OS | Linux CentOS 8 |
| Web Server | NGINX (리버스 프록시 / 정적 파일 서빙) |
| CI/CD | GitHub Actions (push 이벤트 자동 배포) |
| 알림 | Discord (빌드·배포 결과 알림) |

### 외부 API
| API | 용도 |
|-----|------|
| OpenAI GPT-4o | AI 여행 플랜 생성 |
| Kakao Maps API | 여행 코스 지도 시각화 |
| Kakao OAuth | 소셜 로그인 |

---

## 아키텍처

```
[Client - React]
       │  HTTPS
       ▼
[NGINX] ─── 정적 파일 서빙
       │  리버스 프록시
       ▼
[Spring Boot :8080]
       │
       ├─── MySQL 8.0 (DB)
       │
       └─── FastAPI :8090 ──► OpenAI GPT-4o
                              (AI 일정 생성 중간 서버)
```

**인증 흐름**: `Spring Security` + `JWT` 이중 토큰 구조
- Access Token: 단기 인증용 (Authorization Header)
- Refresh Token: HttpOnly 쿠키 저장, 자동 갱신

---

## 주요 기능

### 계정 관리 (ACC)
- 이메일 + 비밀번호 로그인 / **카카오 OAuth 소셜 로그인**
- BCrypt 비밀번호 암호화
- Access Token + Refresh Token 이중 구조 (자동 로그인 유지)
- 로그인 5회 실패 시 제한 처리
- 회원 탈퇴 시 개인정보 마스킹 처리

### 여행지 콘텐츠 (볼거리 / 먹거리 / 놀거리 / 잘거리)
- 카테고리·필터 기반 목록 조회 (최신순 / 인기순)
- 장소 상세: 사진 슬라이더, 카카오 지도, 이용정보
- 리뷰 & 평점 CRUD / 신고 시스템 (누적 5회 자동 블라인드)
- 찜하기 기능 (회원 전용)

### AI 여행 일정 생성 (내거리)
- **지역 → 기간 → 테마** 3단계 선택 UI
- FastAPI 서버를 통해 GPT-4o에 프롬프트 전달 → 최적 동선 생성
- 생성 일정 카카오 지도 시각화 (마커 + 동선)
- 일정 저장 / 장소 추가·삭제 / 드래그앤드롭 순서 변경
- 일정 PDF 저장 및 인쇄 기능

### 커뮤니티 (뽐낼거리)
- **핫플거리**: 방문 장소 사진 후기 게시판
- **인생거리**: AI 일정과 연계된 여행 코스 공유
- 해시태그, 좋아요, 댓글 CRUD, 신고 기능
- 인기 해시태그 TOP 5, 최신·인기순 정렬

### 마이페이지 (MBR)
- 회원 정보 수정 / 프로필 이미지 업로드
- 저장 AI 일정 목록 관리 (Soft Delete)
- 찜 목록 / 작성 게시글 관리

### 관리자 대시보드 (ADM)
- 통계 대시보드 (가입자 현황, 게시글 현황)
- 회원·장소·게시글·리뷰·신고 관리
- 공지사항 / FAQ / 공동코드 CRUD

---

## 데이터베이스 구조

총 **27개 테이블** 구성

```
핵심 테이블 관계도

CMM_GROUP_CODE ──< CMM_CODE
                        │
        ┌───────────────┼───────────────────┐
        │               │                   │
      MEMBER          REGION              PLACE ──< PLACE_SEE
        │               │                   │      PLACE_FOOD
        │               └───< PLACE         │      PLACE_PLAY
        │                                   └──< PLACE_SLEEP
        │                                       PLACE_IMG
        │
        ├──< AI_SCHEDULE ──< AI_SCHEDULE_DAY ──< AI_SCHEDULE_PLACE
        │
        ├──< COMMUNITY ──< COMMUNITY_HASHTAG ──< HASHTAG
        │        │        COMMUNITY_FILE_MAP
        │        └──< COMMENT
        │
        ├──< REVIEW
        ├──< REPORT
        └──< PLACE_WISHLIST
```

| 테이블 | 설명 |
|--------|------|
| `MEMBER` | 회원 정보 (JWT Refresh Token 포함) |
| `AI_SCHEDULE` | AI 생성 여행 일정 |
| `AI_SCHEDULE_DAY` | 일정 날짜별 상세 |
| `AI_SCHEDULE_PLACE` | 일정 내 장소 목록 |
| `PLACE` | 여행 장소 (공공 API contentId 매핑) |
| `COMMUNITY` | 커뮤니티 게시글 (핫플거리/인생거리) |
| `REVIEW` | 장소 리뷰 & 평점 |
| `REPORT` | 신고 내역 |

---

## API 엔드포인트

> 상세 API 명세는 Swagger UI (`/swagger-ui/index.html`) 에서 확인 가능합니다.

### 인증
| Method | URI | 설명 |
|--------|-----|------|
| POST | `/api/auth/join` | 회원가입 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) |
| POST | `/api/auth/reissue` | Access Token 재발급 |
| POST | `/api/auth/logout` | 로그아웃 |
| GET | `/api/auth/kakao/callback` | 카카오 소셜 로그인 콜백 |

### 여행지
| Method | URI | 설명 |
|--------|-----|------|
| GET | `/api/places` | 장소 목록 조회 (카테고리·필터·정렬) |
| GET | `/api/places/{plcNo}` | 장소 상세 조회 |
| POST | `/api/places/{plcNo}/wishlist` | 찜 등록/해제 토글 |
| GET | `/api/places/{plcNo}/reviews` | 장소 리뷰 목록 |
| POST | `/api/places/{plcNo}/reviews` | 리뷰 등록 |

### AI 여행 일정
| Method | URI | 설명 |
|--------|-----|------|
| POST | `/api/schedules/generate` | AI 일정 생성 요청 (→ FastAPI 연동) |
| POST | `/api/schedules` | 생성 일정 저장 |
| GET | `/api/schedules` | 내 일정 목록 |
| GET | `/api/schedules/{scheduleId}` | 일정 상세 조회 |
| DELETE | `/api/schedules/{scheduleId}` | 일정 삭제 (Soft Delete) |

### 커뮤니티
| Method | URI | 설명 |
|--------|-----|------|
| GET | `/api/community` | 게시글 목록 (카테고리·정렬) |
| POST | `/api/community` | 게시글 등록 |
| GET | `/api/community/{postId}` | 게시글 상세 |
| PUT | `/api/community/{postId}` | 게시글 수정 |
| DELETE | `/api/community/{postId}` | 게시글 삭제 (Soft Delete) |
| POST | `/api/community/{postId}/like` | 좋아요 토글 |

---

## 프로젝트 구조

```
src/main/java/com/sixsense/
├── config/                  # Security, MyBatis, CORS 설정
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── CorsConfig.java
├── auth/                    # 인증/인가
│   ├── controller/
│   ├── service/
│   ├── jwt/
│   └── oauth/              # 카카오 OAuth
├── member/                  # 회원 관리
├── place/                   # 여행지 (볼거리/먹거리/놀거리/잘거리)
│   ├── controller/
│   ├── service/
│   ├── mapper/             # MyBatis Mapper
│   └── dto/
├── schedule/                # AI 여행 일정
├── community/               # 커뮤니티 (뽐낼거리)
├── review/                  # 리뷰 & 평점
├── report/                  # 신고
├── admin/                   # 관리자
├── cs/                      # 고객지원 (공지/FAQ)
├── common/                  # 공통 코드, 파일 업로드
└── fastapi/                 # FastAPI 연동 클라이언트
```

---

## 시작하기

### 사전 요구사항

- JDK 21+
- MySQL 8.0.26+
- Maven 또는 Gradle

### 설치 및 실행

```bash
# 1. 레포지토리 클론
git clone https://github.com/your-org/gyeonggi-travel-backend.git
cd gyeonggi-travel-backend

# 2. 환경 변수 파일 생성 (아래 '환경 변수' 섹션 참고)
cp .env.example .env

# 3. 데이터베이스 초기화
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p < src/main/resources/sql/data.sql

# 4. 빌드 및 실행
./mvnw spring-boot:run
# 또는
./gradlew bootRun
```

서버 실행 후 `http://localhost:8080` 에서 확인 가능합니다.

---

## 환경 변수

`application.yml` 또는 `.env` 파일에 아래 값들을 설정하세요.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_db?useSSL=false&serverTimezone=Asia/Seoul
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  access-expiration: 1800000      # 30분
  refresh-expiration: 604800000   # 7일

fastapi:
  base-url: http://localhost:8090  # FastAPI 서버 주소

kakao:
  client-id: ${KAKAO_CLIENT_ID}
  redirect-uri: ${KAKAO_REDIRECT_URI}

openai:
  api-key: ${OPENAI_API_KEY}      # FastAPI 서버에서 사용
```

---

## 팀원 소개

| 이름 | 역할 |
|------|------|
| 김영훈 | 백엔드 개발 |
| 김지태 | 팀장 / 백엔드 개발 / 문서 작성 |
| 노송현 | 백엔드 개발 / DB 설계 |
| 소제우 | 백엔드 개발 |
| 손상진 | 프론트엔드 개발 / 화면 설계 |
| 한상인 | 백엔드 개발 |

---

> **관련 레포지토리**
> - [Frontend (React)](https://github.com/mojitt/sst-front)
> - [FastAPI AI Server](https://github.com/mojitt/sst-fastApi)
