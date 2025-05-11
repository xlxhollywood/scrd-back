# 🗝 방탈출 예약/리뷰/동행 플랫폼 백엔드 (SCRD Back-End)

> **웹 스크래핑을 활용한 방탈출 예약/리뷰/동행 통합 플랫폼 - 백엔드 레포지토리**

- **기간**: 2024.12.01 ~ 2025.04.20 (총 5개월)
- **시연 영상**: [YouTube 링크](https://drive.google.com/drive/folders/1C0baog9rQ4LC-XmpKbN3uXVEPXcWIz9O)

—

## 📌 프로젝트 개요

기존 방탈출 카페 예약 시스템의 단편성과 정보 부족 문제를 해결하고자 전국 방탈출 카페의 예약 가능 시간과 테마 정보를 통합 제공하는 웹/모바일 기반 예약 플랫폼을 개발했습니다.

- 실시간 예약 가능 여부 제공
- 조건 기반 테마 검색, 리뷰 등록, 동행 모집 기능 구현
- SNS 로그인 및 JWT 인증
- 실시간 알림 및 유저 레벨/포인트 시스템까지 통합한 **엔드-투-엔드 서비스 플랫폼 구축**

—

## 🧩 주요 기능

### ✅ 인증/회원 시스템 (기여도 100%)
- **Spring Security + JWT 기반 인증 시스템 구축**
- 소셜 로그인(OAuth2.0) 연동 (카카오)
- **RTR(Refresh Token Rotation)** 구조로 UX 개선
- 자동 닉네임 부여 및 탈퇴 처리 로직 포함

### ✅ 테마 API (기여도 100%)
- 관리자 권한 기반 CRUD API 구현
- QueryDSL 기반 필터링 (난이도, 평점, 공포도, 활동성, 지역)
- MongoDB와 연동된 예약 가능 시간 조회
- 추천순 정렬 (리뷰 수 + 평점 가중치 반영)

### ✅ 리뷰 API (기여도 100%)
- 공포도, 활동성, 평점 기반 리뷰 작성 시스템 구축
- 고정 해시태그 등록 및 통계 필터링 지원
- 리뷰 등록 시 자동 포인트 지급 및 회원 레벨 반영

### ✅ 일행 모집 API / 실시간 알림 (기여도 100%)
- 댓글/대댓글 기능 (Self-Reference)
- N+1 문제 해결 위한 JOIN FETCH 최적화
- **SSE(Server-Sent Events)** 기반 실시간 알림 구축
- 모집 날짜 자동 종료 로직 (운영 자동화)

### ✅ 배포 / 인프라 운영 (기여도 100%)
- **Nginx + HTTPS 서버 구성** (Let’s Encrypt SSL 인증서 적용)
- **Docker Compose 기반** 다중 서버 운영
- AWS EC2 기반 인프라 관리
- GitHub Actions 기반 **CI/CD 자동 배포 파이프라인 구축**

—

## 💡 사용 기술 스택

### 🧱 Backend
- **Spring Boot**, Spring Security, JWT, OAuth2.0
- **Redis** (토큰 관리), **MongoDB + MySQL**
- Spring Data JPA, QueryDSL
- Selenium + Kafka (크롤링 → 비동기 저장)

### 🛠 Infra / DevOps
- **Docker**, **Docker Compose**, **Nginx** (Reverse Proxy)
- AWS EC2, S3, Route 53
- GitHub Actions (CI/CD), Let’s Encrypt (HTTPS)

### 📦 기타
- **SSE(Server-Sent Events)** (실시간 알림)
- MongoDB TTL 인덱스 (예약 데이터 자동 삭제)

—

## 👥 팀 구성 및 역할

| 이름 | 역할 |
|———|———|
| 오세훈 | 기획, 백엔드, 인프라, 프론트엔드, 디자인(UI) |
| 김은진 | 디자인 협업 |
| 임성빈 | 프론트엔드 협업 |
| 김경진 | 데이터 마이닝 |
| 이민규 | 모바일 앱 개발 |

> 프로젝트 전반의 아키텍처 및 기능 개발을 주도했으며, **백엔드 전 영역, 배포, CI/CD, 인증 시스템, 실시간 알림, Mongo 기반 예약 관리까지 100% 기여**

—

## 🔗 프로젝트 시연 링크
[📽 시연 영상 전체 보기 (Google Drive)](https://drive.google.com/drive/folders/1C0baog9rQ4LC-XmpKbN3uXVEPXcWIz9O)

—

## 📞 문의
> 개발자 오세훈 · E-mail: saint0325@handong.ac.kr

—

본 프로젝트는 기술적 도전과 실용성을 함께 고려하여 기획/개발된 풀스택 서비스입니다. 현업에서 바로 활용 가능한 구조를 설계하고, 실제 예약/리뷰/동행이라는 사용자 시나리오를 기반으로 제작되었습니다.
