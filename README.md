# Hyfata REST API

**A Production-Ready REST API with OAuth 2.0 + PKCE and Session Management**

Spring Boot 3.4.4 기반의 멀티테넌시 인증 API. Google OAuth, Discord OAuth와 동일한 보안 표준을 따릅니다.

---

## 주요 기능

### OAuth 2.0 Authorization Code Flow + PKCE
- RFC 7636 PKCE 지원 (모바일/데스크톱 앱 보안)
- CSRF 방지 (State 파라미터)
- 일회용 Authorization Code (10분 유효)
- 토큰 로테이션 (Refresh Token 갱신 시 새 토큰 발급)

### 세션 관리
- 다중 기기 로그인 지원 (최대 5개 세션)
- 세션 목록 조회 (기기 정보, IP, 위치)
- 원격 로그아웃 (다른 기기 세션 무효화)
- Redis 기반 토큰 블랙리스트

### 완전한 인증 시스템
- JWT 기반 토큰 (Access: 24시간, Refresh: 7일)
- BCrypt 비밀번호 암호화
- 2FA (이메일 기반)
- 이메일 검증
- 비밀번호 재설정

### 프로덕션 준비
- PostgreSQL 데이터베이스
- Redis (세션 블랙리스트)
- 만료 코드 자동 정리 스케줄러
- 상세 로깅

---

## 빠른 시작

### 필수 요구사항
- Java 17+
- PostgreSQL 12+
- Redis 6+
- Gradle 7.6+

### 환경 변수 설정 (.env)
```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=rest_api
DB_USERNAME=postgres
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key-min-32-characters

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Mail (optional)
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=noreply@example.com
MAIL_PASSWORD=your_password
```

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 테스트 없이 빌드
./gradlew build -x test

# 애플리케이션 실행
./gradlew bootRun
```

---

## API 엔드포인트

### OAuth 2.0 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/oauth/authorize` | Authorization 요청 (로그인 페이지) |
| POST | `/oauth/login` | 사용자 로그인 처리 |
| POST | `/oauth/token` | Token 발급 (`authorization_code`, `refresh_token`) |
| POST | `/oauth/logout` | OAuth 로그아웃 (인증 필요) |

### 세션 관리 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/api/sessions` | 활성 세션 목록 조회 |
| DELETE | `/api/sessions/{sessionId}` | 특정 세션 무효화 |
| DELETE | `/api/sessions/others` | 현재 세션 외 모두 무효화 |

### 클라이언트 관리

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | `/api/clients/register` | 새 클라이언트 등록 |
| GET | `/api/clients/{clientId}` | 클라이언트 정보 조회 |

### 레거시 API (Deprecated)

| 메서드 | 엔드포인트 | 상태 |
|--------|-----------|------|
| POST | `/api/auth/register` | 사용 가능 (회원가입) |
| POST | `/api/auth/login` | **Deprecated** - OAuth 사용 권장 |

---

## OAuth 2.0 + PKCE 플로우

```
클라이언트 앱                              Hyfata API
     │
     ├─ 1. code_verifier 생성 (128자)
     ├─ 2. code_challenge = SHA256(verifier)
     │
     └─ 3. GET /oauth/authorize ──────────────────>
           ?client_id=...
           &redirect_uri=...
           &code_challenge=...
           &code_challenge_method=S256
                                          └─ 로그인 페이지 반환
     │
     ├─ 4. 사용자 로그인 ─────────────────────────>
                                          └─ Authorization Code 발급
     │
     ├─ 5. POST /oauth/token ─────────────────────>
           grant_type=authorization_code
           &code=...
           &code_verifier=...             └─ PKCE 검증
           &client_id=...                 └─ 세션 생성
           &client_secret=...
                                          └─ Access + Refresh Token 발급
     │
     └─ 6. 토큰 사용
```

---

## 테스트

### Postman 컬렉션
`test/` 폴더에 완전한 테스트 컬렉션이 포함되어 있습니다:

- `OAuth2_PKCE_Complete_Testing.json` - Postman 컬렉션
- `OAUTH2_PKCE_TESTING.md` - 테스트 가이드

### 테스트 실행
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "*JwtUtilTest*"

# 테스트 결과 보기
# build/reports/tests/test/index.html
```

---

## 보안 기능

| 기능 | 설명 |
|------|------|
| **PKCE** | Authorization Code 탈취 방지 (RFC 7636) |
| **State** | CSRF 공격 방지 |
| **토큰 로테이션** | Refresh 시 새 토큰 발급, 기존 무효화 |
| **JTI 블랙리스트** | 로그아웃 시 Access Token 즉시 무효화 |
| **세션 제한** | 사용자당 최대 5개 동시 세션 |
| **BCrypt** | 비밀번호 해싱 (Salt 자동 생성) |

---

## 아키텍처

```
┌─────────────────────────────────────────┐
│  Hyfata REST API                        │
├─────────────────────────────────────────┤
│  ┌─────────────────────────────────┐    │
│  │  OAuth 2.0 + PKCE Layer         │    │
│  │  - Authorization Code Flow      │    │
│  │  - Token Exchange               │    │
│  │  - PKCE Verification            │    │
│  └─────────────────────────────────┘    │
│                  ↓                      │
│  ┌─────────────────────────────────┐    │
│  │  Session Management Layer       │    │
│  │  - Multi-device Support         │    │
│  │  - Token Rotation               │    │
│  │  - JTI Blacklist (Redis)        │    │
│  └─────────────────────────────────┘    │
│                  ↓                      │
│  ┌─────────────────────────────────┐    │
│  │  Authentication Layer           │    │
│  │  - JWT Token Management         │    │
│  │  - Password Hashing             │    │
│  │  - 2FA/Email Verification       │    │
│  └─────────────────────────────────┘    │
│                  ↓                      │
│  ┌─────────────────────────────────┐    │
│  │  Data Layer                     │    │
│  │  - PostgreSQL (JPA)             │    │
│  │  - Redis (Blacklist)            │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

---

## 데이터베이스 스키마

| 테이블 | 목적 |
|--------|------|
| `users` | 사용자 정보 및 인증 |
| `clients` | OAuth 클라이언트 정보 |
| `authorization_codes` | Authorization Code 저장 |
| `user_sessions` | 사용자 세션 정보 |

---

## 문서

**상세 문서는 [Wiki](https://github.com/Hyfata/Hyfata-API/wiki)에서 확인하세요:**

- [OAuth 2.0 + PKCE 구현](https://github.com/Hyfata/Hyfata-API/wiki/OAuth-2.0-Authorization-Code-Flow)
- [세션 관리](https://github.com/Hyfata/Hyfata-API/wiki/Session-Management)
- [데이터베이스 스키마](https://github.com/Hyfata/Hyfata-API/wiki/Database-Schema-&-Guide)

---

## 의존성

| Component | Version |
|-----------|---------|
| Spring Boot | 3.4.4 |
| Java | 17 |
| PostgreSQL | 12+ |
| Redis | 6+ |
| JJWT | 0.12.3 |

---

## 향후 계획

- [x] OAuth 2.0 + PKCE 지원
- [x] 세션 관리 (다중 기기)
- [x] 토큰 로테이션
- [ ] OAuth 2.0 Scopes 세분화
- [ ] Rate Limiting
- [ ] WebAuthn 지원

---

## 라이선스

MIT License

---

**Made with care for secure multi-tenant authentication**
