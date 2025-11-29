# Session Management Testing Guide

이 가이드는 Postman을 사용하여 세션 관리 API를 테스트하는 방법을 설명합니다.

## Prerequisites

### 1. Redis 서버 실행
세션 블랙리스트 기능을 위해 Redis가 필요합니다.

```bash
# Docker를 사용하는 경우
docker run -d --name redis -p 6379:6379 redis:latest

# 또는 로컬 Redis 사용
redis-server
```

### 2. 환경 변수 설정 (.env)
```properties
REDIS_HOST=localhost
REDIS_PORT=6379
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

---

## Postman Environment Setup

### 변수 설정
Postman Environment에 다음 변수를 추가하세요:

| Variable | Initial Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8080` | API 서버 주소 |
| `accessToken` | (빈 값) | 로그인 후 자동 설정 |
| `refreshToken` | (빈 값) | 로그인 후 자동 설정 |

---

## API Endpoints

### 1. 회원가입

**POST** `{{baseUrl}}/api/auth/register`

```json
{
    "email": "test@example.com",
    "password": "Password123!",
    "username": "testuser"
}
```

**Response (201 Created):**
```json
{
    "success": true,
    "message": "User registered successfully"
}
```

---

### 2. 로그인

**POST** `{{baseUrl}}/api/auth/login`

```json
{
    "email": "test@example.com",
    "password": "Password123!"
}
```

**Response (200 OK):**
```json
{
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 900000
}
```

**Post-request Script (토큰 자동 저장):**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("accessToken", response.accessToken);
    pm.environment.set("refreshToken", response.refreshToken);
}
```

---

### 3. 활성 세션 목록 조회

현재 로그인된 모든 기기/세션을 조회합니다.

**GET** `{{baseUrl}}/api/sessions`

**Headers:**
```
Authorization: Bearer {{accessToken}}
```

**Response (200 OK):**
```json
{
    "sessions": [
        {
            "sessionId": "abc123def456...",
            "deviceType": "Desktop",
            "deviceName": "Chrome on Windows",
            "ipAddress": "192.168.1.100",
            "location": "Seoul, South Korea",
            "lastActiveAt": "2024-01-15T10:30:00",
            "createdAt": "2024-01-15T09:00:00",
            "isCurrent": true
        },
        {
            "sessionId": "xyz789...",
            "deviceType": "Mobile",
            "deviceName": "Safari on iPhone",
            "ipAddress": "192.168.1.105",
            "location": "Seoul, South Korea",
            "lastActiveAt": "2024-01-15T08:00:00",
            "createdAt": "2024-01-14T15:00:00",
            "isCurrent": false
        }
    ],
    "totalCount": 2,
    "maxAllowed": 5
}
```

---

### 4. 특정 세션 무효화 (원격 로그아웃)

다른 기기의 세션을 원격으로 종료합니다.

**DELETE** `{{baseUrl}}/api/sessions/{sessionId}`

**Headers:**
```
Authorization: Bearer {{accessToken}}
```

**Path Variables:**
- `sessionId`: 무효화할 세션 ID (세션 목록에서 확인)

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Session revoked successfully"
}
```

**Response (404 Not Found):**
```json
{
    "success": false,
    "message": "Session not found"
}
```

---

### 5. 현재 세션 외 모든 세션 무효화

현재 사용 중인 세션을 제외한 모든 세션을 종료합니다.

**DELETE** `{{baseUrl}}/api/sessions/others`

**Headers:**
```
Authorization: Bearer {{accessToken}}
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "All other sessions revoked successfully",
    "revokedCount": 3
}
```

---

### 6. 로그아웃

**POST** `{{baseUrl}}/api/auth/logout`

**Headers:**
```
Authorization: Bearer {{accessToken}}
```

**Request Body:**
```json
{
    "refreshToken": "{{refreshToken}}"
}
```

**Response (200 OK):**
```json
{
    "success": true,
    "message": "Logged out successfully"
}
```

---

### 7. 토큰 갱신

**POST** `{{baseUrl}}/api/auth/refresh`

```json
{
    "refreshToken": "{{refreshToken}}"
}
```

**Response (200 OK):**
```json
{
    "success": true,
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 900000
}
```

**Post-request Script:**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("accessToken", response.accessToken);
    pm.environment.set("refreshToken", response.refreshToken);
}
```

---

## Test Scenarios

### Scenario 1: 기본 세션 관리 흐름

1. **회원가입** - 새 계정 생성
2. **로그인** - Access/Refresh 토큰 획득
3. **세션 목록 조회** - 현재 세션 확인
4. **로그아웃** - 세션 종료

### Scenario 2: 다중 기기 로그인 테스트

1. **Device 1에서 로그인** (Chrome)
2. **Device 2에서 로그인** (Postman에서 User-Agent 변경)
   - Headers에 추가: `User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)`
3. **세션 목록 조회** - 2개 세션 확인
4. **특정 세션 무효화** - Device 1 세션 종료
5. **세션 목록 조회** - 1개 세션만 남음 확인

### Scenario 3: 동시 세션 제한 테스트 (최대 5개)

1. 5개의 서로 다른 User-Agent로 로그인
2. 6번째 로그인 시도
3. 가장 오래된 세션이 자동으로 무효화되는지 확인
4. 세션 목록에서 최대 5개만 있는지 확인

### Scenario 4: 토큰 블랙리스트 테스트

1. **로그인**
2. **로그아웃** (세션 무효화)
3. **민감한 API 호출** (`/api/sessions`)
4. **결과 확인**: `401 Unauthorized` + `"Token has been revoked"`

### Scenario 5: 토큰 갱신 흐름

1. **로그인** - 토큰 획득
2. **15분 대기** (또는 만료된 토큰 사용)
3. **Refresh 토큰으로 갱신**
4. **새 토큰으로 API 호출**

---

## User-Agent Examples for Multi-Device Testing

다양한 기기를 시뮬레이션하려면 Postman Headers에서 `User-Agent`를 변경하세요:

### Desktop Browsers
```
# Chrome on Windows
Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36

# Safari on macOS
Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Safari/605.1.15

# Firefox on Linux
Mozilla/5.0 (X11; Linux x86_64; rv:121.0) Gecko/20100101 Firefox/121.0
```

### Mobile Devices
```
# Safari on iPhone
Mozilla/5.0 (iPhone; CPU iPhone OS 17_2_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Mobile/15E148 Safari/604.1

# Chrome on Android
Mozilla/5.0 (Linux; Android 14; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.210 Mobile Safari/537.36
```

### Tablets
```
# Safari on iPad
Mozilla/5.0 (iPad; CPU OS 17_2_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Mobile/15E148 Safari/604.1
```

---

## Error Responses

### 401 Unauthorized
```json
{
    "error": "Token has been revoked"
}
```
- 원인: 블랙리스트에 등록된 토큰으로 민감한 API 접근 시도
- 해결: 다시 로그인하여 새 토큰 획득

### 403 Forbidden
```json
{
    "error": "Cannot revoke other user's session"
}
```
- 원인: 다른 사용자의 세션을 무효화하려는 시도
- 해결: 본인 소유의 세션만 무효화 가능

### 404 Not Found
```json
{
    "success": false,
    "message": "Session not found"
}
```
- 원인: 존재하지 않거나 이미 무효화된 세션
- 해결: 세션 목록을 다시 조회하여 유효한 세션 ID 확인

---

## Security Notes

1. **Access Token**: 15분 만료, 일반 API는 JWT 서명만 검증
2. **Refresh Token**: 2주 만료, DB에 해시 저장
3. **민감한 API**: Redis 블랙리스트 추가 검증
   - `/api/sessions/**`
   - `/api/auth/change-password`
   - `/api/users/me`
   - `/api/payments/**`
4. **동시 세션 제한**: 최대 5개, 초과 시 가장 오래된 세션 자동 무효화
5. **토큰 회전**: Refresh 시 새로운 Access/Refresh 토큰 쌍 발급
