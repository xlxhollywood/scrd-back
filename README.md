```markdown
# 🚀 **로그인 기능 구현**

**🗓 작성일: 2024-12-30**

---

## 📘 **프로젝트 설명**

웹스크래핑을 이용한 통합 뷰어 및 방탈출 소셜 매칭 서비스 SCRD

---

## 🌟 **주요 기능**

### 1️⃣ **JWT 인증 및 인가**  
- **Access Token 및 Refresh Token 발급**  
- **Access Token 만료 처리**:  
  - **클라이언트**에서 Access Token의 만료 시간을 확인하며,  
    만료 시에만 Refresh Token과 함께 서버로 요청을 보냅니다.  
- **JWT 필터 동작**:  
  - Access Token만 전달된 경우 **서명을 통한 권한 확인**을 처리합니다.

### 2️⃣ **Refresh Token 처리**  
- **Redis를 이용한 Refresh Token 저장 및 검증**  
- **Redis 데이터 구조**:  
  - Key: `회원식별 ID`  
  - Value: `refreshtokenId`  
- **서버 동작**:  
  - Refresh Token을 Redis와 대조하여 **유효성을 검증**합니다.

### 3️⃣ **RTR 방식 (Refresh Token Rotation)**  
- **Access Token이 만료된 경우**:  
  - **Access Token + Refresh Token**을 요청 헤더에 포함하여 서버로 전달합니다.  
  - Refresh Token 검증 후 **새 Access Token 및 Refresh Token 발급**.  
- **Access Token만 유효한 경우**:  
  - JWT 필터를 통해 **서명을 통한 권한 확인**.

### 4️⃣ **소셜 로그인**  
- **소셜 로그인 지원** (Google 등)  
- **일반 로그인은 미지원**.

---

## 🛠 **기술 스택**

- **Spring Boot**: 백엔드 애플리케이션 프레임워크  
- **Spring Security**: 인증 및 인가 처리  
- **JWT (JSON Web Token)**: 토큰 기반 인증  
- **Redis**: Refresh Token 저장소 및 검증  


  
---

## 🗂 **데이터베이스 설계 (Redis)**

- **Key**: 회원식별 ID  
- **Value**: RefreshToken ID  

---

## ⚡️ **참고 사항**

- **Access Token의 만료 시간**은 **클라이언트**에서 확인 후,  
  만료 시에만 Refresh Token과 함께 요청을 보냅니다.  
- **Redis**를 통해 Refresh Token의 유효성을 관리하며,  
  토큰 재발급 시 기존 Refresh Token을 삭제하고 **새로운 Refresh Token을 발급**합니다.

---  

> **✅ 이 프로젝트는 소셜 로그인을 기반으로 안전한 인증 및 인가 방식을 제공합니다.**
```
