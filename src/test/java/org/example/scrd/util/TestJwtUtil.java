package org.example.scrd.util;

import io.jsonwebtoken.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 테스트용 JWT 유틸리티
 * Redis 저장 없이 JWT 토큰만 생성
 */
public class TestJwtUtil {
    
    public static List<String> createToken(Long userId, String secretKey, long expireTimeMs, long expireRefreshTimeMs) {
        // JWT의 payload에 해당하는 Claims에 데이터를 추가
        Claims claims = Jwts.claims();
        claims.put("userId", userId); // 사용자 ID를 Claim에 넣음

        // Base64로 인코딩된 키 사용 (실제 JwtUtil과 동일하게)
        String base64SecretKey = java.util.Base64.getEncoder().encodeToString(secretKey.getBytes());

        //Access Token 발급
        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .claim("tokenType", "ACCESS") // 토큰 타입 추가
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, base64SecretKey) // Base64 인코딩된 키 사용
                .compact();

        // Refresh Token 발급 (테스트용으로 Redis 저장 없음)
        String refreshToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .claim("tokenType", "REFRESH") // 토큰 타입 추가
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
                .setExpiration(new Date(System.currentTimeMillis() + expireRefreshTimeMs)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, base64SecretKey) // Base64 인코딩된 키 사용
                .compact();

        // 액세스, 리프레쉬가 들어가 있는 토큰 객체를 반환
        return Arrays.asList(accessToken, refreshToken);
    }

    // JWT에서 userId 추출하는 메서드
    public static Long getUserId(String token, String secretKey) {
        // 토큰에서 Claim을 추출하고 userId를 반환
        return extractClaims(token, secretKey).get("userId", Long.class);
    }

    // SecretKey를 사용해 Token을 검증하고, Claim을 추출하는 메서드
    private static Claims extractClaims(String token, String secretKey) {
        try {
            // Base64로 인코딩된 키 사용
            String base64SecretKey = java.util.Base64.getEncoder().encodeToString(secretKey.getBytes());
            
            // 토큰을 파싱하여 Claim을 추출
            return Jwts.parser()
                    .setSigningKey(base64SecretKey)  // Base64 인코딩된 키 사용
                    .parseClaimsJws(token) // 토큰을 파싱하고 유효성 검사를 수행
                    .getBody(); // 유효한 경우 토큰의 본문(Claim)을 반환
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 토큰입니다.");
        }
    }
}
