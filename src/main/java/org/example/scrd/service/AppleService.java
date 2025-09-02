package org.example.scrd.service;

import org.example.scrd.dto.UserDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@Service
public class AppleService {
    
    private static final Logger logger = LoggerFactory.getLogger(AppleService.class);
    
    @Value("${apple.api.key.key-id}")
    private String keyId;
    
    @Value("${apple.api.key.team-id}")
    private String teamId;
    
    @Value("${apple.api.key.client-id}")
    private String clientId;
    
    @Value("${apple.api.key.private-key}")
    private String privateKey;

    public UserDto appleLogin(String code, String redirectUri) {
        String accessToken = getAccessToken(code, redirectUri);
        return getAppleUserInfo(accessToken);
    }

    private String getAccessToken(String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> appleTokenRequest = new HttpEntity<>(body, headers);

        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://appleid.apple.com/auth/token",
                    HttpMethod.POST,
                    appleTokenRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            logger.error("🔥 [애플 토큰 발급 실패] message: {}", e.getMessage());
            throw new RuntimeException("애플 토큰 발급 실패", e);
        }
    }

    private UserDto getAppleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> appleUserInfoRequest = new HttpEntity<>(headers);
        
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://appleid.apple.com/auth/userinfo",
                    HttpMethod.GET,
                    appleUserInfoRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String sub = jsonNode.get("sub").asText();
            String email = jsonNode.has("email") ? jsonNode.get("email").asText() : "";
            String name = jsonNode.has("name") ? jsonNode.get("name").asText() : "";

            return UserDto.builder()
                    .kakaoId(Long.parseLong(sub)) // 애플의 sub를 kakaoId 필드에 저장
                    .email(email)
                    .name(name)
                    .profileImageUrl("") // 애플은 기본 프로필 이미지를 제공하지 않음
                    .build();

        } catch (Exception e) {
            logger.error("🔥 [애플 사용자 정보 조회 실패] message: {}", e.getMessage());
            throw new RuntimeException("애플 사용자 정보 조회 실패", e);
        }
    }
}
