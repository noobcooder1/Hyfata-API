# Hyfata REST API

**A Production-Ready REST API with OAuth 2.0 Authorization Code Flow**

Spring Boot 3.4.4 기반의 멀티테넌시 인증 API. Google OAuth, Discord OAuth와 동일한 보안 표준을 따릅니다.

---

## 🌟 주요 기능

### ✅ OAuth 2.0 Authorization Code Flow
- 보안 기반의 인증 흐름
- 여러 사이트/앱 동시 지원
- CSRF 방지 (State 파라미터)
- 일회용 Authorization Code
- 🆕 **PKCE 지원** (RFC 7636) - 모바일 앱 보안

### ✅ 완전한 인증 시스템
- JWT 기반 토큰 (Access + Refresh)
- BCrypt 비밀번호 암호화
- 2FA (2-Factor Authentication)
- 이메일 검증
- 비밀번호 재설정

### ✅ 멀티테넌시 지원
- 클라이언트별 독립적인 frontendUrl
- 동적 이메일 링크 생성
- 클라이언트별 권한 관리

### ✅ 프로덕션 준비
- PostgreSQL 데이터베이스
- Flyway 데이터베이스 마이그레이션
- 자동 정리 스케줄러
- 상세 로깅

---

## 📋 빠른 시작

### 필수 요구사항
- Java 17+
- PostgreSQL 12+
- Gradle 7.6+

### 빌드 및 실행

```bash
# 빌드
./gradlew build

# 테스트 포함 빌드
./gradlew build

# 테스트 없이 빌드
./gradlew build -x test

# 애플리케이션 실행
./gradlew bootRun
```

### 데이터베이스 설정

`src/main/resources/application.properties`에서 PostgreSQL 연결 정보 수정:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rest_api
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## 🔑 핵심 API 엔드포인트

### OAuth 2.0 엔드포인트

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/oauth/authorize` | Authorization 요청 → 로그인 페이지 |
| POST | `/oauth/login` | 사용자 로그인 처리 |
| POST | `/oauth/token` | Authorization Code → Token 교환 |

### 클라이언트 관리

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | `/api/clients/register` | 새 클라이언트 등록 |
| GET | `/api/clients/{clientId}` | 클라이언트 정보 조회 |

### 레거시 API (권장하지 않음)

| 메서드 | 엔드포인트 | 상태 |
|--------|-----------|------|
| POST | `/api/auth/register` | ⚠️ DEPRECATED |
| POST | `/api/auth/login` | ⚠️ DEPRECATED |

---

## 📚 문서

**모든 상세 문서는 [Wiki](https://github.com/Hyfata/Hyfata-RestAPI/wiki)에서 확인하세요:**

### Getting Started
- [Installation & Setup](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Installation-&-Setup)
- [Configuration & Environment Variables](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Configuration-&-Environment-Variables)
- [Database Setup](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Database-Setup)

### OAuth 2.0 Documentation
- [OAuth 2.0 Authorization Code Flow](https://github.com/Hyfata/Hyfata-RestAPI/wiki/OAuth-2.0-Authorization-Code-Flow)
- [PKCE Implementation (RFC 7636)](https://github.com/Hyfata/Hyfata-RestAPI/wiki/PKCE-Implementation)
- [Implementation Summary](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Implementation-Summary)

### Authentication System
- [API Authentication & JWT](https://github.com/Hyfata/Hyfata-RestAPI/wiki/API-Authentication-&-JWT)
- [Authentication Implementation Details](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Authentication-Implementation-Details)

### Infrastructure
- [Database Schema & Guide](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Database-Schema-&-Guide)
- [Email Service & Configuration](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Email-Service-&-Configuration)
- [Mail Server Setup](https://github.com/Hyfata/Hyfata-RestAPI/wiki/Mail-Server-Setup)

---

## 🔐 보안 기능

### Authorization Code
- ✅ **일회용**: 한 번 사용 후 사용 불가능
- ✅ **만료**: 10분 유효
- ✅ **정리**: 만료된 코드 자동 삭제

### Token Security
- ✅ **JWT 기반**: 표준 형식 사용
- ✅ **Access Token**: 24시간 유효
- ✅ **Refresh Token**: 7일 유효
- ✅ **HttpOnly 쿠키**: XSS 방지

### CSRF Protection
- ✅ **State Parameter**: 세션 상태 추적
- ✅ **Redirect URI**: 화이트리스트 검증

### PKCE (Proof Key for Code Exchange)
- ✅ **code_challenge**: SHA-256 기반 생성
- ✅ **code_verifier**: 클라이언트 측에서만 관리
- ✅ **Authorization Code 탈취 방지**: 코드 탈취 시에도 토큰 획득 불가
- ✅ **모바일 앱 최적화**: Flutter, React Native 등 모바일 앱용

### Password Security
- ✅ **BCrypt**: 업계 표준 암호화
- ✅ **Salt**: 자동 salt 생성

---

## 📊 아키텍처

```
┌─────────────────────────────────────────┐
│  Hyfata REST API                        │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐  │
│  │  OAuth 2.0 + PKCE Layer         │  │
│  │  - Authorization Code Flow      │  │
│  │  - Token Exchange               │  │
│  │  - PKCE (RFC 7636)              │  │
│  └─────────────────────────────────┘  │
│                  ↓                     │
│  ┌─────────────────────────────────┐  │
│  │  Authentication Layer           │  │
│  │  - JWT Token Management         │  │
│  │  - Password Hashing             │  │
│  │  - 2FA/Email Verification       │  │
│  └─────────────────────────────────┘  │
│                  ↓                     │
│  ┌─────────────────────────────────┐  │
│  │  Service Layer                  │  │
│  │  - Business Logic               │  │
│  │  - Email Service                │  │
│  │  - Client Management            │  │
│  └─────────────────────────────────┘  │
│                  ↓                     │
│  ┌─────────────────────────────────┐  │
│  │  Repository Layer               │  │
│  │  - Database Access              │  │
│  │  - JPA Persistence              │  │
│  └─────────────────────────────────┘  │
│                  ↓                     │
│  ┌─────────────────────────────────┐  │
│  │  PostgreSQL Database            │  │
│  │  - Users                        │  │
│  │  - Clients                      │  │
│  │  - Authorization Codes          │  │
│  └─────────────────────────────────┘  │
│                                         │
└─────────────────────────────────────────┘
```

---

## 💾 데이터베이스 스키마

### 주요 테이블

| 테이블 | 목적 |
|--------|------|
| `users` | 사용자 정보 및 인증 |
| `clients` | OAuth 클라이언트 정보 |
| `authorization_codes` | Authorization Code 저장 |

---

## 🚀 사용 예시

### 1단계: 클라이언트 등록

```bash
curl -X POST http://localhost:8080/api/clients/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Web App",
    "frontendUrl": "https://myapp.com",
    "redirectUris": ["https://myapp.com/callback"],
    "maxTokensPerUser": 5
  }'
```

응답:
```json
{
  "client": {
    "clientId": "client_1697406234567_4829",
    "clientSecret": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
    ...
  }
}
```

### 2단계: 사용자 로그인 흐름

```
1. 프론트엔드 → GET /oauth/authorize
2. 사용자 로그인
3. API → Authorization Code 발급
4. 백엔드 → POST /oauth/token (code 교환)
5. API → Access Token 발급
6. 백엔드 → 토큰을 HttpOnly 쿠키에 저장
```

---

## 🧪 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "*JwtUtilTest*"

# 테스트 결과 보기
# build/reports/tests/test/index.html
```

---

## 📦 의존성

### 핵심 의존성
- **Spring Boot**: 3.4.4
- **Spring Security**: JWT 기반 인증
- **Spring Data JPA**: ORM
- **PostgreSQL Driver**: 데이터베이스
- **Lombok**: 보일러플레이트 감소
- **JJWT**: JWT 라이브러리
- **Spring Mail**: 이메일 발송
- **Thymeleaf**: 로그인 페이지 뷰

---

## 🔧 설정

### application.properties

```properties
# 데이터베이스
spring.datasource.url=jdbc:postgresql://localhost:5432/rest_api
spring.datasource.username=postgres
spring.datasource.password=...

# JWT
jwt.secret=your-secret-key-min-32-characters
jwt.expiration=86400000

# 메일
spring.mail.host=mail.hyfata.kr
spring.mail.port=587
spring.mail.username=noreply@hyfata.kr
spring.mail.password=...
spring.mail.from=noreply@hyfata.kr

# OAuth
oauth.default-client.enabled=true

# 스케줄러
spring.task.scheduling.pool.size=2
```

---

## 📈 모니터링 및 로깅

```bash
# 로그 레벨 설정
logging.level.kr.hyfata.rest.api=INFO
logging.level.org.springframework.security=DEBUG
```

---

## 🤝 기여

버그 리포트, 기능 요청, 또는 개선사항은 이슈를 통해 알려주세요.

---

## 📝 라이선스

MIT License

---

## 🎯 향후 계획

- [ ] OAuth 2.0 Implicit Flow (SPA용)
- [x] PKCE 지원 (모바일 앱용) ✅ **완료**
- [ ] Scopes 세분화
- [ ] Rate Limiting
- [ ] WebAuthn 지원
- [ ] 감시 및 분석

---

## 📞 문제 해결

### 포트 충돌
```bash
# 다른 포트에서 실행
./gradlew bootRun --args='--server.port=8081'
```

### 데이터베이스 초기화
```bash
# 마이그레이션 다시 실행
./gradlew flywayClean flywayMigrate
```

### 로그 확인
```bash
# 상세 로그 보기
./gradlew bootRun --info
```

---

## 📚 참고 자료

- [OAuth 2.0 공식 스펙](https://tools.ietf.org/html/rfc6749)
- [PKCE (RFC 7636)](https://tools.ietf.org/html/rfc7636) - 🆕 모바일 앱 보안
- [Spring Security 문서](https://spring.io/projects/spring-security)
- [JWT 소개](https://jwt.io)
- [OWASP 보안 가이드](https://owasp.org/)
- [Google OAuth 2.0 PKCE 구현](https://developers.google.com/identity/protocols/oauth2/native-app)

---

**Made with ❤️ for secure multi-tenant authentication**
