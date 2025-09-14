#!/bin/bash

echo "🚀 서버 배포 스크립트 시작..."

# 1. 깃허브에서 최신 코드 가져오기
echo "📥 깃허브에서 최신 코드 가져오는 중..."
git checkout dev
git pull origin dev

# 2. 기존 컨테이너들 정리
echo "🧹 기존 컨테이너들 정리 중..."
docker-compose down

# 3. 최신 도커 이미지 가져오기
echo "📦 최신 도커 이미지 가져오는 중..."
docker pull ohsehun/spring-backend:latest

# 4. 전체 스택 시작
echo "🏃 전체 스택 시작 중..."
docker-compose up -d

# 5. 상태 확인
echo "⏳ 애플리케이션 시작 대기 중..."
sleep 15

echo "📊 컨테이너 상태 확인:"
docker-compose ps

echo "🔍 애플리케이션 로그 (최근 10줄):"
docker-compose logs backend --tail=10

echo "✅ 배포 완료!"
echo "🌐 API 테스트: curl http://localhost:8080/actuator/health"
